package com.meteor.aifadianpay.afdian.request;

import com.google.gson.annotations.SerializedName;

public class AfdOrderReq {
    @SerializedName("page")
    private Integer page;
    @SerializedName("out_trade_no")
    private String out_trade_no;

    public AfdOrderReq(Integer page) {
        this.page = page;
    }

    public AfdOrderReq(Integer page, String out_trade_no) {
        this.page = page;
        this.out_trade_no = out_trade_no;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public void setOut_trade_no(String out_trade_no) {
        this.out_trade_no = out_trade_no;
    }

    public Integer getPage() {
        return page;
    }

    public String getOut_trade_no() {
        return out_trade_no;
    }
}