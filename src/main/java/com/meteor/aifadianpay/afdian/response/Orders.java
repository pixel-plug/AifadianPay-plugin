package com.meteor.aifadianpay.afdian.response;


import com.google.gson.annotations.SerializedName;
import com.meteor.aifadianpay.afdian.request.AfadianRequest;

import java.util.List;
public class Orders {
    @SerializedName("list")
    private List<Order> orderList;
    @SerializedName("total_count")
    private int totalCount;
    @SerializedName("total_page")
    private int totalPage;
    @SerializedName("request")
    private AfadianRequest requst;

    public List<Order> getOrderList() {
        return orderList;
    }

    public void setOrderList(List<Order> orderList) {
        this.orderList = orderList;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public AfadianRequest getRequst() {
        return requst;
    }

    public void setRequst(AfadianRequest requst) {
        this.requst = requst;
    }
}