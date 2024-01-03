package com.meteor.aifadianpay.afdian.request;

import com.google.gson.annotations.SerializedName;

public class AfdOrderReq {

    private Integer page;
    @SerializedName("out_trade_no")
    private String out_trade_no;

    public AfdOrderReq(Integer page) {
        this.page = page;
    }

    public AfdOrderReq(String out_trade_no) {
        this.out_trade_no = out_trade_no;
    }

    /**
     * 当为0时防止被序列化以查询特定编号订单
     * @return
     */
    @SerializedName("page")
    public Integer getPage(){
        return page==0?null:page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public void setOut_trade_no(String out_trade_no) {
        this.out_trade_no = out_trade_no;
    }


    public String getOut_trade_no() {
        return out_trade_no;
    }
}