package com.meteor.aifadianpay.storage;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.meteor.aifadianpay.AifadianPay;
import com.meteor.aifadianpay.afdian.response.Order;
import com.meteor.aifadianpay.storage.export.ExportOrder;
import com.meteor.aifadianpay.storage.export.ExportSkuDetail;

import java.io.*;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 存储处理
 */
public abstract class AbstractStorage implements IStorage{


    public static final String ORDER_TABLE = "AIFADIAN_ORDERS_0";
    public static final String SKU_DETAIL_TABLE = "AIFADIAN_SKUDETAIL_0";

    public abstract AifadianPay getPlugin();

    public abstract Connection getConnection();

    /**
     * 查询所有订单记录
     * @return
     */
    @Override
    public List<ExportOrder> queryAllOrder() {
        List<ExportOrder> orders = new ArrayList<>();
        Connection connection = getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement("select * from " + ORDER_TABLE);
             ResultSet resultSet = preparedStatement.executeQuery()
        ){
            while (resultSet.next()){
                Order order = new Order();
                order.setOutTradeNo((String) resultSet.getObject("out_trade_no"));
                order.setRemark((String) resultSet.getObject("remark"));
                order.setUserId((String)resultSet.getObject("user_id"));
                order.setPlanTitle((String) resultSet.getObject("plan_title"));
                order.setRedeemId((String) resultSet.getObject("redeem_id"));
                order.setTotalAmount((String)resultSet.getObject("price"));
                ExportOrder exportOrder = new ExportOrder();
                exportOrder.setOrder(order);
                exportOrder.setInsertTime((long) resultSet.getObject("insert_time"));
                orders.add(exportOrder);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        getPlugin().getLogger().info("订单表记录数: "+orders.size());
        return orders;
    }

    /**
     * 查询所有型号记录
     * @return
     */
    @Override
    public List<ExportSkuDetail> queryAllSkudetail() {

        List<ExportSkuDetail> skuDetails = new ArrayList<>();

        Connection connection = getConnection();
        try (            PreparedStatement preparedStatement = connection.prepareStatement("select * from " + SKU_DETAIL_TABLE);
                         ResultSet resultSet = preparedStatement.executeQuery()
        ){
            while (resultSet.next()){
                ExportSkuDetail exportSkuDetail = new ExportSkuDetail();
                exportSkuDetail.setOut_trade_no((String) resultSet.getObject("out_trade_no"));
                exportSkuDetail.setSku_id((String) resultSet.getObject("sku_id"));
                exportSkuDetail.setPrice((String) resultSet.getObject("price"));
                exportSkuDetail.setName((String) resultSet.getObject("name"));
                exportSkuDetail.setCount((Integer) resultSet.getObject("count"));
                skuDetails.add(exportSkuDetail);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        getPlugin().getLogger().info("型号表记录数: "+skuDetails.size());
        return skuDetails;
    }

    private void export(Object obj,File path){
        long currentTimeMillis = System.currentTimeMillis();
        String json = new Gson().toJson(obj);
        try {
            try(FileWriter fileWriter = new FileWriter(path)) {
                fileWriter.write(json);
                fileWriter.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
        getPlugin().getLogger().info("导出完成，耗时 "+(System.currentTimeMillis()-currentTimeMillis)+"ms");
    }

    /**
     * 导出数据
     */
    public void Export(String exportSign){
        File dataFolder = new File(getPlugin().getDataFolder(),"backup");
        if(!dataFolder.exists()) dataFolder.mkdirs();
        this.export(queryAllOrder(),new File(dataFolder,"order-"+exportSign+".json"));
        this.export(queryAllSkudetail(),new File(dataFolder,"skudetail-"+exportSign+".json"));
    }


    /**
     * 将json转换为对象集合
     * @param readPath
     * @param
     * @return
     * @param <T>
     */
    private <T> List<T> json2ObjectList(File readPath, Type typeOfT) {

        if(!readPath.exists()) return null;

        try {
            FileReader fileReader = new FileReader(readPath);
            return new Gson().fromJson(fileReader, typeOfT);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }



    /**
     * 导入数据
     */
    public void Import(String importSign){

        long currentTimeMillis = System.currentTimeMillis();


        File dataFolder = new File(getPlugin().getDataFolder(),"backup");
        List<ExportOrder> exportOrders = json2ObjectList(new File(dataFolder, "order-" + importSign + ".json"), new TypeToken<List<ExportOrder>>() {
        }.getType());

        if(exportOrders==null){
            getPlugin().getLogger().info("数据记录不存在...");
            return;
        }

        List<ExportSkuDetail> exportSku = json2ObjectList(new File(dataFolder, "order-" + importSign + ".json"), new TypeToken<List<ExportSkuDetail>>() {
        }.getType());

        importData(exportOrders,exportSku);

        getPlugin().getLogger().info("导入了"+(exportSku.size()+exportOrders.size())+"条记录,耗时:" +(System.currentTimeMillis()-currentTimeMillis)+"ms");


    }

}
