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

    public void success(QueryOrderResponse queryOrderResponse) {
        Orders orders = queryOrderResponse.getOrders();
        if(orders!=null){
            AifadianPay.INSTANCE.getLogger().info("正在处理第"+orders.getTotalPage()+"/"+orders.getTotalCount()+"页");
            orders.getOrderList().forEach(order -> iStorage.handeOrder(order));
        }
    }

}
