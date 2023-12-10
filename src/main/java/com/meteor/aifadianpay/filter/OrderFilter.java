package com.meteor.aifadianpay.filter;

import com.meteor.aifadianpay.afdian.response.Order;

import java.util.List;

/**
 * 订单过滤器
 */
public interface OrderFilter {

    List<Order> meet(List<Order> orders);

}
