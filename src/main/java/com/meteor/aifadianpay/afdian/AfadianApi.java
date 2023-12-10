package com.meteor.aifadianpay.afdian;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.meteor.aifadianpay.AifadianPay;
import com.meteor.aifadianpay.Status;
import com.meteor.aifadianpay.afdian.request.AfadianRequest;
import com.meteor.aifadianpay.afdian.request.AfdOrderReq;
import com.meteor.aifadianpay.afdian.response.Orders;
import com.meteor.aifadianpay.afdian.response.QueryOrderResponse;
import com.meteor.aifadianpay.httputil.Http;
import com.meteor.aifadianpay.httputil.HttpHeaders;
import com.meteor.aifadianpay.httputil.callback.AsyncHttpResponseCallBack;
import com.meteor.aifadianpay.httputil.request.HttpRequest;
import com.meteor.aifadianpay.httputil.response.PackHttpResponse;
import org.bukkit.configuration.file.FileConfiguration;

public class AfadianApi {

    private String user_id;
    private String token;

    public final static Gson gson = new Gson();

    private HttpRequest queryOrdersRequest;


    public static AfadianApi afadianApi;

    public static void init(String user_id,String token){
        afadianApi = new AfadianApi(user_id,token);
        Status ping = afadianApi.ping();
        if(ping==Status.N200){
            AifadianPay.INSTANCE.getLogger().info("api状态正常!");
        }else {
            AifadianPay.INSTANCE.getLogger().info("api异常,错误信息"+ping.getTip()+",请检查config内api信息填写是否正确");
            AifadianPay.INSTANCE.getLogger().info("稍后你可以输入/apl ping来重新验证");
        }
    }

    private AfadianApi(String user_id,String token){
        this.user_id = user_id;
        this.token = token;
        this.queryOrdersRequest = Http.url("https://afdian.net/api/open/query-order")
                .header(HttpHeaders.ACCEPT_CHARSET, "utf-8")
                .header(HttpHeaders.CONTENT_TYPE, "application/json");
    }

    /**
     * 解析响应订单
     */
    public QueryOrderResponse toOrders(PackHttpResponse packHttpResponse){
        if(AifadianPay.debug){
            System.out.println("开始解析json");
        }
        String asString = packHttpResponse.getAsString("UTF-8");
        QueryOrderResponse queryOrderResponse = gson.fromJson(asString, QueryOrderResponse.class);
        if(AifadianPay.debug){
            System.out.println("query");
        }
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
    public Status ping(){
        AfdOrderReq afdOrderReq = new AfdOrderReq(1);
        AfadianRequest afadianRequest = new AfadianRequest();
        afadianRequest.setToken(this.token);
        afadianRequest.setUser_id(this.user_id);
        afadianRequest.setParam(afdOrderReq);
        PackHttpResponse post = queryOrdersRequest.body(gson.toJson(afadianRequest.init()))
                .post();
        int status = post.getStatus();
        return Status.match(status);
    }
}
