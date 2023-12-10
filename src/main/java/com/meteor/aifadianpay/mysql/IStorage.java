package com.meteor.aifadianpay.mysql;

public interface IStorage {

    /**
     * 开启数据库
     */
    void enable();

    /**
     * 关闭数据库
     */
    void disable();
}
