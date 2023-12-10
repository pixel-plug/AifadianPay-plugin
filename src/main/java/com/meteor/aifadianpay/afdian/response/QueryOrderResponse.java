package com.meteor.aifadianpay.afdian.response;

import com.google.gson.annotations.SerializedName;

public class QueryOrderResponse {

    private int ec;
    private String em;
    @SerializedName("data")
    private Orders orders;

    public int getEc() {
        return ec;
    }

    public void setEc(int ec) {
        this.ec = ec;
    }

    public String getEm() {
        return em;
    }

    public void setEm(String em) {
        this.em = em;
    }

    public Orders getOrders() {
        return orders;
    }

    public void setOrders(Orders orders) {
        this.orders = orders;
    }
}
