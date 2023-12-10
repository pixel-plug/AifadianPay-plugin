package com.meteor.aifadianpay.httputil.request;

import org.apache.http.client.methods.*;


public enum RequestMethod {
    GET(){
        @Override
        public HttpRequestBase get(String url) {
            return new HttpGet(url);
        }
    },POST(){
        @Override
        public HttpRequestBase get(String url) {
            return new HttpPost(url);
        }
    },DELETE(){
        @Override
        public HttpRequestBase get(String url) {
            return new HttpDelete(url);
        }
    },PUT(){
        @Override
        public HttpRequestBase get(String url) {
            return new HttpPut(url);
        }
    },TRACE(){
        @Override
        public HttpRequestBase get(String url) {
            return new HttpTrace(url);
        }
    };

    /**
     * 获取HttpRequestBase的实现类 (HttpPost,HttpDelete....)
     * @param url
     * @return
     */
    public HttpRequestBase get(String url) {
        return null;
    }
}
