package com.meteor.aifadianpay.httputil.callback;

import com.meteor.aifadianpay.httputil.response.PackHttpResponse;

/**
 * 异步请求回调
 */
public interface AsyncHttpResponseCallBack {

    /**
     * success
     * @param packHttpResponse
     */
    void success(PackHttpResponse packHttpResponse);

    /**
     * fail
     */
    void fail(Exception e);
}
