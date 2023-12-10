package com.meteor.aifadianpay.storage;

import com.meteor.aifadianpay.afdian.response.Order;

public interface IStorage {

    /**
     * 处理订单
     */
    void handeOrder(Order order);


}
