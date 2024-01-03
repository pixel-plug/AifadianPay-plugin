package com.zsenhe.test;

import com.google.gson.JsonObject;
import com.meteor.aifadianpay.afdian.AfadianApi;
import com.meteor.aifadianpay.httputil.Http;
import com.meteor.aifadianpay.httputil.HttpHeaders;
import com.meteor.aifadianpay.httputil.callback.AsyncHttpResponseCallBack;
import com.meteor.aifadianpay.httputil.response.PackHttpResponse;

/**
 * 测试拉取商品自动创建
 */
public class TestPack {
    public static void main(String[] args) {
        Http.url("https://afdian.net/api/creator/get-plan-skus?plan_id=6da20b76aa1011eeb63f5254001e7c00&is_ext=")
                .timeOut(3000)
                .header(HttpHeaders.ACCEPT, "application/json")
                .asyncGet(new AsyncHttpResponseCallBack() {
                    @Override
                    public void success(PackHttpResponse packHttpResponse) {
                        String asString = packHttpResponse.getAsString("UTF-8");
                        System.out.println(asString);
                        JsonObject jsonObject = AfadianApi.gson.fromJson(asString, JsonObject.class);

                    }

                    @Override
                    public void fail(Exception e) {

                    }
                });
    }
}
