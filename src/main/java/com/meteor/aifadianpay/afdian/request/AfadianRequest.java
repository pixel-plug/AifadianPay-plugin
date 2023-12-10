package com.meteor.aifadianpay.afdian.request;


import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import com.meteor.aifadianpay.afdian.AfadianApi;

import javax.rmi.ssl.SslRMIClientSocketFactory;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class AfadianRequest {
    private String user_id;
    transient private String token;
    private long ts;
    private String sign;

    transient private AfdOrderReq param;
    private String params;

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getTs() {
        return ts;
    }

    public void setTs(long ts) {
        this.ts = ts;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }


    public void setParam(AfdOrderReq param) {
        this.param = param;
    }

    public AfdOrderReq getParam() {
        return param;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public void setPrams(String params) {
        this.params = params;
    }

    /**
     * 构建密钥
     * @return
     */
    public AfadianRequest init() {
        try {
            this.ts = System.currentTimeMillis() / 1000;
            this.params = AfadianApi.gson.toJson(getParam());
            String p = token + "params" + params  + "ts" + ts + "user_id" + user_id;
            MessageDigest md = MessageDigest.getInstance("MD5");
            this.sign = new BigInteger(1, md.digest(p.getBytes(StandardCharsets.UTF_8))).toString(16);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return this;
    }
}