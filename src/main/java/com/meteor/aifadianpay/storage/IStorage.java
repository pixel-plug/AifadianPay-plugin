package com.meteor.aifadianpay.storage;

import com.meteor.aifadianpay.afdian.response.Order;

public interface IStorage {



    /**
     * 处理订单
     */
    boolean handeOrder(Order order,boolean b);

    /**
     * 获取玩家捐赠总数
     * @param p
     * @return
     */
    int queryPlayerDonate(String p);

    boolean isHandleOrder(String tradeNo);

}
