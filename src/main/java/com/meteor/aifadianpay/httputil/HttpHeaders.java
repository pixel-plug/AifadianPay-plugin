package com.meteor.aifadianpay.httputil;

public enum HttpHeaders {
    ACCEPT("Accept", "指定客户端能够接收的内容类型"),
    ACCEPT_CHARSET("Accept-Charset", "浏览器可以接受的字符集"),
    ACCEPT_ENCODING("Accept-Encoding", "指定浏览器可以支持的压缩编码类型"),
    ACCEPT_LANGUAGE("Accept-Language", "浏览器可接受的语言"),
    CACHE_CONTROL("Cache-Control", "指定请求和响应遵循的缓存机制"),
    CONNECTION("Connection", "表示是否需要持久连接（HTTP 1.1默认进行持久连接）"),
    COOKIE("Cookie", "HTTP请求发送时，会把保存在该请求域名下的所有cookie值一起发送给web服务器"),
    CONTENT_LENGTH("Content-Length", "表示请求消息体的长度"),
    CONTENT_TYPE("Content-Type", "表示请求消息体的媒体类型"),
    HOST("Host", "表示请求的服务器的域名和端口号"),
    REFERER("Referer", "表示请求的来源页面"),
    USER_AGENT("User-Agent", "客户端标识，告诉服务器客户端的信息");
    
    private final String key;
    private final String description;

    HttpHeaders(String key, String description) {
        this.key = key;
        this.description = description;
    }

    public String getKey() {
        return key;
    }

    public String getDescription() {
        return description;
    }
}
