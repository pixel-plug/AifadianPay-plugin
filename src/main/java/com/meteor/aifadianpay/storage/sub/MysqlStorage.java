package com.meteor.aifadianpay.storage.sub;

import com.meteor.aifadianpay.AifadianPay;
import com.meteor.aifadianpay.afdian.response.Order;
import com.meteor.aifadianpay.afdian.response.SkuDetail;
import com.meteor.aifadianpay.api.event.SendOutGoodsEvent;
import com.meteor.aifadianpay.data.ShopItem;
import com.meteor.aifadianpay.filter.FilterManager;
import com.meteor.aifadianpay.mysql.FastMySQLStorage;
import com.meteor.aifadianpay.mysql.column.Column;
import com.meteor.aifadianpay.mysql.data.KeyValue;
import com.meteor.aifadianpay.storage.AbstractStorage;
import com.meteor.aifadianpay.storage.export.ExportOrder;
import com.meteor.aifadianpay.storage.export.ExportSkuDetail;
import com.meteor.aifadianpay.util.BaseConfig;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class MysqlStorage extends AbstractStorage {

    private AifadianPay plugin;
    private FastMySQLStorage fastMySQLStorage;


    public MysqlStorage(AifadianPay plugin){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
            } catch (ClassNotFoundException ex) {
                ex.printStackTrace();
                plugin.getLogger().info("无法加载数据库驱动,请反馈给开发者");
                return;
            }
        }
        this.plugin = plugin;
        this.fastMySQLStorage = new FastMySQLStorage(plugin,plugin.getConfig().getConfigurationSection("mysql-info"));
        this.connect();
        // 启用事务
        try {
            this.getConnection().setAutoCommit(false);
            plugin.getLogger().info("已禁用数据库自动提交");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }





    private void connect(){
        this.fastMySQLStorage.enable();
        this.createTable();
    }

    /**
     * 创建相关表
     */
    private void createTable(){
        // 订单表
        Column[] orders = {
                Column.of("out_trade_no",Column.Char.VARCHAR,true,40),
                Column.of("remark",Column.Char.VARCHAR,100),
                Column.of("user_id",Column.Char.VARCHAR,40),
                Column.of("plan_title",Column.Char.VARCHAR,40),
                Column.of("redeem_id ", Column.Char.VARCHAR,40),
                Column.of("price",Column.Char.VARCHAR,40),
                Column.of("insert_time",Column.Integer.BIGINT,15)
        };

        // 购买型号表
        Column[] skuDetails = {
                Column.of("out_trade_no",Column.Char.VARCHAR,40),
                Column.of("sku_id",Column.Char.VARCHAR,40),
                Column.of("price",Column.Char.VARCHAR,40),
                Column.of("name",Column.Char.VARCHAR,40),
                Column.of("count",Column.Integer.INT,10)
        };

        this.fastMySQLStorage.createTable(ORDER_TABLE,orders);
        this.fastMySQLStorage.createTable(SKU_DETAIL_TABLE,skuDetails);
        plugin.getLogger().info("com.meteor.aifadianpay.mysql");
    }

    // 是否存在指定已处理订单
    private boolean isExistOrder(String trade_no){
        return fastMySQLStorage.isExists(ORDER_TABLE,"out_trade_no",trade_no);
    }

    /**
     * 处理订单
     * @param order
     */
    @Override
    public boolean handeOrder(Order order,boolean isSave) {
        // 过滤订单
        List<Order> orders = FilterManager.meet(Arrays.asList(order));
        if(!orders.isEmpty()){
            Order handleOrder = orders.get(0);
            Player playerExact = Bukkit.getPlayerExact(handleOrder.getRemark());

            Connection connection = getConnection();

            if(playerExact!=null&&isSave){

                try {
                    if(!isExistOrder(handleOrder.getOutTradeNo())){

                        /***
                         * 插入已处理订单表
                         */
                        KeyValue[] tradeLog = {
                                new KeyValue("out_trade_no",handleOrder.getOutTradeNo()),
                                new KeyValue("remark",handleOrder.getRemark()),
                                new KeyValue("user_id",handleOrder.getUserId()),
                                new KeyValue("plan_title",handleOrder.getPlanTitle()),
                                new KeyValue("redeem_id",handleOrder.getRedeemId()),
                                new KeyValue("price",handleOrder.getTotalAmount()),
                                new KeyValue("insert_time",System.currentTimeMillis())
                        };
                        fastMySQLStorage.put(ORDER_TABLE,tradeLog);

                        /***
                         * 型号处理
                         */
                        for (SkuDetail skuDetail : handleOrder.getSkuDetail()) {
                            KeyValue[] skuDetailLog = {
                                    new KeyValue("out_trade_no",handleOrder.getOutTradeNo()),
                                    new KeyValue("sku_id",skuDetail.getSkuId()),
                                    new KeyValue("price",skuDetail.getPic()),
                                    new KeyValue("name",skuDetail.getName()),
                                    new KeyValue("count",skuDetail.getCount())
                            };
                            fastMySQLStorage.put(SKU_DETAIL_TABLE,skuDetailLog);
                        }

                        // 提交事务
                        connection.commit();

                        /**
                         * 发货
                         */
                        ShopItem shopItem = BaseConfig.STORE.getShopItemMap().get(handleOrder.getPlanTitle());
                        Bukkit.getScheduler().runTask(plugin,()->{
                            SendOutGoodsEvent sendOutGoodsEvent = new SendOutGoodsEvent(playerExact,shopItem,handleOrder.getOutTradeNo(),handleOrder.getSkuDetail(),handleOrder);
                            Bukkit.getServer().getPluginManager().callEvent(sendOutGoodsEvent);
                        });
                        return true;
                    }
                }catch (SQLException sqlException){
                    try {
                        connection.rollback();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return false;
    }

    public Connection getConnection(){
        try {
            return fastMySQLStorage.getConnection();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int queryPlayerDonate(String p) {
        PreparedStatement preparedStatement = null;
        try {
            String sql = "select sum(cast(`price` as DECIMAL(10,2))) as count from "+ORDER_TABLE+" where remark = ?";
            preparedStatement = fastMySQLStorage.getConnection().prepareStatement(sql);
            preparedStatement.setString(1,p);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) return resultSet.getInt("sum");
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }finally {
            try {
                preparedStatement.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return 0;
    }

    @Override
    public boolean isHandleOrder(String tradeNo) {
        return isExistOrder(tradeNo);
    }

    @Override
    public void importData(List<ExportOrder> exportOrders, List<ExportSkuDetail> skuDetails) {
        for (ExportOrder exportOrder : exportOrders) {
            Order order = exportOrder.getOrder();
            KeyValue[] keyValues = {
                    new KeyValue("out_trade_no",order.getOutTradeNo()),
                    new KeyValue("remark",order.getRemark()),
                    new KeyValue("user_id",order.getUserId()),
                    new KeyValue("plan_title",order.getPlanTitle()),
                    new KeyValue("redeem_id",order.getRedeemId()),
                    new KeyValue("price",order.getTotalAmount()),
                    new KeyValue("insert_time",exportOrder.getInsertTime())
            };
            fastMySQLStorage.put(ORDER_TABLE,keyValues);
        }

        for (ExportSkuDetail skuDetail : skuDetails) {
            KeyValue[] keyValues = {
                    new KeyValue("out_trade_no",skuDetail.getOut_trade_no()),
                    new KeyValue("sku_id",skuDetail.getSku_id()),
                    new KeyValue("price",skuDetail.getPrice()),
                    new KeyValue("name",skuDetail.getName()),
                    new KeyValue("count",skuDetail.getCount())
            };
            fastMySQLStorage.put(SKU_DETAIL_TABLE,keyValues);
        }

    }

    @Override
    public AifadianPay getPlugin() {
        return plugin;
    }
}
