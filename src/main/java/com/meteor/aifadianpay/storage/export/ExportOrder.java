package com.meteor.aifadianpay.storage.export;

import com.meteor.aifadianpay.afdian.response.Order;

public class ExportOrder {

    private Order order;
    private long insertTime;

    public Order getOrder(){
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public long getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(long insertTime) {
        this.insertTime = insertTime;
    }
}
