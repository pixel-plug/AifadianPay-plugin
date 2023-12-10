package com.zsenhe.test;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.meteor.aifadianpay.afdian.request.AfadianRequest;
import com.meteor.aifadianpay.afdian.request.AfdOrderReq;
import com.meteor.aifadianpay.afdian.response.Orders;
import com.meteor.aifadianpay.afdian.response.QueryOrderResponse;
import com.meteor.aifadianpay.afdian.response.SkuDetail;
import com.meteor.aifadianpay.httputil.Http;
import com.meteor.aifadianpay.httputil.HttpHeaders;
import com.meteor.aifadianpay.httputil.callback.AsyncHttpResponseCallBack;
import com.meteor.aifadianpay.httputil.response.PackHttpResponse;


public class TestHttp {

    public static void main(String[] args) {

        SkuDetail skuDetail;

        AfadianRequest afadianRequest = new AfadianRequest();
        afadianRequest.setToken("5jk6Y8yE7THeWr3ctvPaVhnCRBDKFwp4");
        afadianRequest.setUser_id("0736f39e716111ed97ca52540025c377");
        afadianRequest.setParam(new AfdOrderReq(1));
        afadianRequest.init();


        Http.url("https://afdian.net/api/open/query-order").
                header(HttpHeaders.ACCEPT_CHARSET, "utf-8")
                .header(HttpHeaders.CONTENT_TYPE, "application/json")
                .body((new Gson()).toJson(afadianRequest))
                .asyncPost(new AsyncHttpResponseCallBack() {
                    @Override
                    public void success(PackHttpResponse packHttpResponse) {
                        final QueryOrderResponse queryOrderResponse;
                        String asString = packHttpResponse.getAsString("UTF-8");
                        queryOrderResponse = (new Gson()).fromJson(asString, QueryOrderResponse.class);
                        queryOrderResponse.getOrders().getOrderList().forEach(order -> System.out.println(order.toString()));
                        System.out.println(queryOrderResponse.getOrders().getTotalPage());
                    }

                    @Override
                    public void fail(Exception e) {

                    }
                });

    }

}
