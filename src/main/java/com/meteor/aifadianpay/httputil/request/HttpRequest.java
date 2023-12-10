package com.meteor.aifadianpay.httputil.request;


import com.meteor.aifadianpay.httputil.HttpHeaders;
import com.meteor.aifadianpay.httputil.callback.AsyncHttpResponseCallBack;
import com.meteor.aifadianpay.httputil.response.PackHttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.apache.http.impl.client.HttpClientBuilder;

public class HttpRequest implements AutoCloseable{

    public final static CloseableHttpClient httpClient = HttpClientBuilder.create().build();

    /**
     * 使用异步请求时的线程池
     */
    public final static Executor threadPool = Executors.newCachedThreadPool();

    private String url;

    private Map<HttpHeaders,String> headersMap;

    private Map<String,String> pathParams;

    private Map<String,String> queryParams;

    private String body;

    private int timeOut = -1;


    public HttpRequest(String url) {
        this.url = url;
    }



    public HttpRequest header(HttpHeaders httpHeaders,String value){
        if(headersMap == null) headersMap = new HashMap<>();
        headersMap.put(httpHeaders,value);
        return this;
    }

    public HttpRequest body(String body){
        this.body = body;
        return this;
    }

    public HttpRequest timeOut(int timeOut){
        this.timeOut = timeOut;
        return this;
    }

    public HttpRequest pathParam(String k,String v){
        if(pathParams == null || pathParams.isEmpty()) pathParams = new HashMap<>();
        pathParams.put(k,v);
        return this;
    }

    public HttpRequest queryParam(String k,String v){
        if(queryParams== null || queryParams.isEmpty()) queryParams = new HashMap<>();
        queryParams.put(k,v);
        return this;
    }

    // 替换url占位符
    private String packPathParams(){
        if(pathParams == null || pathParams.isEmpty()) return url;

        return pathParams.entrySet().stream()
                .reduce(url,(r,entry) -> r.replace("{"+entry.getKey()+"}",entry.getValue()),
                        (r1,r2) -> r2);
    }

    // 替换url查询参数
    private String packUrlParams(){
        if(queryParams == null || queryParams.isEmpty()) return url;

        StringBuilder stringBuilder = new StringBuilder(url+"?");

        return queryParams.entrySet().stream()
                .reduce(stringBuilder,(r,entry) -> stringBuilder.append(entry.getKey()+"="+entry.getValue())
                        ,(r1,r2) ->r2).toString();
    }

    // 拼接URL
    public String buildURL() throws MalformedURLException {
        // 替换url占位符
        url = packPathParams();
        // 替换url查询参数
        url = packUrlParams();

        return url;

    }

    /**
     * 发起异步请求
     * @param callBack
     * @return
     */
    public void executeAsync(RequestMethod requestMethod, AsyncHttpResponseCallBack callBack){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                PackHttpResponse execute = null;
                try{
                    execute = execute(requestMethod);
                    callBack.success(execute);
                } catch (Exception e) {
                    callBack.fail(e);
                }finally {
                    try {
                        execute.close();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        };
        HttpRequest.threadPool.execute(runnable);
    }

    /**
     * 发起http请求
     * @param requestMethod 请求方式
     * @return PackHttpResponse
     * @throws IOException
     */
    public PackHttpResponse execute(RequestMethod requestMethod) throws Exception {
        HttpRequestBase httpRequestBase = requestMethod.get(buildURL());

        // 当请求可以接受请求体
        if(body!=null && httpRequestBase instanceof HttpEntityEnclosingRequestBase) {
            HttpEntityEnclosingRequestBase httpEntityEnclosingRequestBase = (HttpEntityEnclosingRequestBase)httpRequestBase;
            httpEntityEnclosingRequestBase.setEntity(new StringEntity(this.body,"UTF-8"));
        }

        if(timeOut!=-1){
            // 设置超时
            RequestConfig.Builder config = RequestConfig.custom();
            config.setConnectTimeout(timeOut);
            config.setSocketTimeout(timeOut);
            config.setConnectionRequestTimeout(timeOut);
        }

        // 设置请求头
        if(headersMap != null && !headersMap.isEmpty()){
            headersMap.entrySet().stream().reduce(httpRequestBase,(result,entrySet) -> {
                result.addHeader(entrySet.getKey().getKey(),entrySet.getValue());
                return result;
            },(r1,r2)->r2);
        }

        return new PackHttpResponse(httpClient.execute(httpRequestBase));
    }


    public PackHttpResponse get(){
        try {
            return execute(RequestMethod.GET);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public PackHttpResponse post(){
        try {
            return execute(RequestMethod.POST);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void asyncGet(AsyncHttpResponseCallBack callBack){
        executeAsync(RequestMethod.GET,callBack);
    }

    public void asyncPost(AsyncHttpResponseCallBack callBack){
        executeAsync(RequestMethod.POST,callBack);
    }


    @Override
    public void close() throws Exception {
        httpClient.close();
    }
}
