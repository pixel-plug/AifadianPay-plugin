package com.meteor.aifadianpay.httputil.response;


import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import java.io.IOException;


/**
 * 包装httpclient请求响应
 */
public class PackHttpResponse implements AutoCloseable{
    /**
     * httpclient 原始的响应类
     */
    private HttpResponse httpResponse;

    public PackHttpResponse(HttpResponse httpResponse){
        this.httpResponse = httpResponse;
    }

    /**
     * 是否成功响应
     * @return 成功响应 返回 true
     */
    public boolean isSuccess(){
        return httpResponse.getStatusLine().getStatusCode() == 200;
    }

    /**
        关闭实体输入流
     */
    @Override
    public void close() {
        try {
            httpResponse.getEntity().getContent().close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取响应状态码
     */
    public int getStatus(){
        return httpResponse.getStatusLine().getStatusCode();
    }


    /**
     * 将响应体以字符串返回
     * @param charset 使用的编码
     * @return 字符串
     * @throws IOException
     */
    public String getAsString(String charset) {
        try {
            return EntityUtils.toString(httpResponse.getEntity(),charset);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] getAsBytes(){
        try {
            return EntityUtils.toByteArray(httpResponse.getEntity());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
