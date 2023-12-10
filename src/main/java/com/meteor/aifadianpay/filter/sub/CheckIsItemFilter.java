package com.meteor.aifadianpay.filter.sub;

import com.meteor.aifadianpay.afdian.response.Order;
import com.meteor.aifadianpay.filter.OrderFilter;

import java.util.List;
import java.util.stream.Collectors;

/***
 * 检查订单是否为商品
 */
public class CheckIsItemFilter implements OrderFilter {
    @Override
    public List<Order> meet(List<Order> orders) {
        return orders.stream().filter(order -> order.getProductType()==1).collect(Collectors.toList());
    }
}
