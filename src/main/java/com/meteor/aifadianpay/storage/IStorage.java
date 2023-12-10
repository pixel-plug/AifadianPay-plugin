package com.meteor.aifadianpay.storage;

import com.meteor.aifadianpay.afdian.response.Order;

public interface IStorage {

    /**
     * 处理订单
     */
    void handeOrder(Order order);

    /**
     * 获取玩家捐赠总数
     * @param p
     * @return
     */
    int queryPlayerDonate(String p);

}
