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
import com.meteor.aifadianpay.storage.IStorage;
import com.meteor.aifadianpay.util.BaseConfig;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.checkerframework.checker.units.qual.C;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class MysqlStorage implements IStorage {

    private AifadianPay plugin;
    private FastMySQLStorage fastMySQLStorage;

    private final String ORDER_TABLE = "AIFADIAN_ORDERS_0";
    private final String SKU_DETAIL_TABLE = "AIFADIAN_SKUDETAIL_0";


    public MysqlStorage(AifadianPay plugin){
        this.plugin = plugin;
        this.fastMySQLStorage = new FastMySQLStorage(plugin,plugin.getConfig().getConfigurationSection("mysql-info"));
        this.connect();
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
    public void handeOrder(Order order,boolean isSave) {
        // 过滤订单
        List<Order> orders = FilterManager.meet(Arrays.asList(order));
        if(!orders.isEmpty()){
            Order handleOrder = orders.get(0);
            Player playerExact = Bukkit.getPlayerExact(handleOrder.getRemark());

            if(playerExact!=null&&isSave){
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

                    /**
                     * 发货
                     */
                    ShopItem shopItem = BaseConfig.STORE.getShopItemMap().get(handleOrder.getPlanTitle());
                    Bukkit.getScheduler().runTask(plugin,()->{
                        SendOutGoodsEvent sendOutGoodsEvent = new SendOutGoodsEvent(playerExact,shopItem,handleOrder.getOutTradeNo(),handleOrder.getSkuDetail(),handleOrder);
                        Bukkit.getServer().getPluginManager().callEvent(sendOutGoodsEvent);
                    });
                }
            }

        }

    }

    @Override
    public int queryPlayerDonate(String p) {
        PreparedStatement preparedStatement = null;
        try {
            String sql = "select sum(price) as count from "+ORDER_TABLE+" where remark = ?";
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
}
