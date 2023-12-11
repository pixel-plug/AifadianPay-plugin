package com.meteor.aifadianpay.afdian.handle;

import com.meteor.aifadianpay.AifadianPay;
import com.meteor.aifadianpay.afdian.AfadianApi;
import com.meteor.aifadianpay.afdian.response.Orders;
import com.meteor.aifadianpay.afdian.response.QueryOrderResponse;
import com.meteor.aifadianpay.httputil.callback.AsyncHttpResponseCallBack;
import com.meteor.aifadianpay.httputil.response.PackHttpResponse;
import com.meteor.aifadianpay.storage.IStorage;

public class HandlerQueryOrdersResponse{

    private IStorage iStorage;

    public HandlerQueryOrdersResponse(IStorage iStorage){
        this.iStorage = iStorage;
    }

    public void success(QueryOrderResponse queryOrderResponse,boolean isSave) {
        Orders orders = queryOrderResponse.getOrders();


        if(orders!=null){
            if(AifadianPay.debug) {
                AifadianPay.INSTANCE.getLogger().info("本页订单数 " + orders.getOrderList().size());
            }
            orders.getOrderList().forEach(order -> {
                iStorage.handeOrder(order,isSave);
            });
        }
    }

}
