package com.meteor.aifadianpay.mysql.data;

public class KeyValue<V> {

    private String key;
    private V value;

    public KeyValue(String key, V value) {
        this.key = key;
        this.value = value;
    }

    public V getValue() {
        return value;
    }

    public String getKey() {
        return key;
    }
}
