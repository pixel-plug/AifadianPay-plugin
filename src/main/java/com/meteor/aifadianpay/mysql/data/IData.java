package com.meteor.aifadianpay.mysql.data;

public interface IData {

    boolean isRead();

    void read(Runnable runnable);

    void save(Runnable runnable);
}
