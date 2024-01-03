package com.meteor.aifadianpay.afdian;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.meteor.aifadianpay.AifadianPay;
import com.meteor.aifadianpay.Status;
import com.meteor.aifadianpay.afdian.request.AfadianRequest;
import com.meteor.aifadianpay.afdian.request.AfdOrderReq;
import com.meteor.aifadianpay.afdian.response.Order;
import com.meteor.aifadianpay.afdian.response.Orders;
import com.meteor.aifadianpay.afdian.response.QueryOrderResponse;
import com.meteor.aifadianpay.httputil.Http;
import com.meteor.aifadianpay.httputil.HttpHeaders;
import com.meteor.aifadianpay.httputil.callback.AsyncHttpResponseCallBack;
import com.meteor.aifadianpay.httputil.request.HttpRequest;
import com.meteor.aifadianpay.httputil.response.PackHttpResponse;
import com.meteor.aifadianpay.util.BaseConfig;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;

public class AfadianApi {

    private String user_id;
    private String token;

    public final static Gson gson = new Gson();

    private HttpRequest queryOrdersRequest;


    public static AfadianApi afadianApi;

    public static void init(String user_id,String token){
        afadianApi = new AfadianApi(user_id,token);
    }



    private AfadianApi(String user_id,String token){
        this.user_id = user_id;
        this.token = token;
        this.queryOrdersRequest = Http.url("https://afdian.net/api/open/query-order")
                .header(HttpHeaders.ACCEPT_CHARSET, "utf-8")
                .timeOut(BaseConfig.STORE.getHttpTimeout())
                .header(HttpHeaders.CONTENT_TYPE, "application/json");
    }

    /**
     * 解析响应订单
     */
    public QueryOrderResponse toOrders(PackHttpResponse packHttpResponse){
        if(AifadianPay.debug){
            AifadianPay.INSTANCE.getLogger().info("开始解析json");
        }
        String asString = packHttpResponse.getAsString("UTF-8");
        QueryOrderResponse queryOrderResponse = gson.fromJson(asString, QueryOrderResponse.class);
        if(AifadianPay.debug){
            AifadianPay.INSTANCE.getLogger().info("返回状态码: "+queryOrderResponse.getEc());
        }
        packHttpResponse.close();
        return queryOrderResponse;
    }


    /**
     * 异步请求订单
     * @param afdOrderReq
     * @param callBack
     */
    public void queryOrders(AfdOrderReq afdOrderReq,AsyncHttpResponseCallBack callBack){
        AfadianRequest afadianRequest = new AfadianRequest();
        afadianRequest.setToken(this.token);
        afadianRequest.setUser_id(this.user_id);
        afadianRequest.setParam(afdOrderReq);
        String body = gson.toJson(afadianRequest.init());
        queryOrdersRequest.body(body).asyncPost(callBack);
    }

    /**
     * 请求订单
     */
    public PackHttpResponse queryOrders(AfdOrderReq afdOrderReq){
        AfadianRequest afadianRequest = new AfadianRequest();
        afadianRequest.setToken(this.token);
        afadianRequest.setUser_id(this.user_id);
        afadianRequest.setParam(afdOrderReq);
        String body = gson.toJson(afadianRequest.init());
        return queryOrdersRequest.body(body).post();
    }

    /**
     * 测试api状态
     */
    public static void ping(CommandSender commandSender){
        AfdOrderReq afdOrderReq = new AfdOrderReq(1);
        AfadianRequest afadianRequest = new AfadianRequest();
        afadianRequest.setToken(AfadianApi.afadianApi.token);
        afadianRequest.setUser_id(AfadianApi.afadianApi.user_id);
        afadianRequest.setParam(afdOrderReq);
        AfadianApi.afadianApi.queryOrdersRequest.body(gson.toJson(afadianRequest.init()))
                .asyncPost(new AsyncHttpResponseCallBack() {
                    @Override
                    public void success(PackHttpResponse packHttpResponse) {
                        QueryOrderResponse queryOrderResponse = AfadianApi.afadianApi.toOrders(packHttpResponse);

                        Status match = Status.match(queryOrderResponse.getEc());
                        Map<String,String> params = new HashMap<>();
                        params.put("@tip@",match.getTip());
                        if(match==Status.N200){
                            commandSender.sendMessage(BaseConfig.STORE.getMessageBox().getMessage(params,"message.ping.success"));
                        }else {
                            commandSender.sendMessage(BaseConfig.STORE.getMessageBox().getMessage(params,"message.ping.fail"));
                        }
                    }

                    @Override
                    public void fail(Exception e) {
                        e.printStackTrace();
                    }
                });
    }
}
