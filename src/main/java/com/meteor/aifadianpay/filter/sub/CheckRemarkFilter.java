package com.meteor.aifadianpay.filter.sub;

import com.meteor.aifadianpay.AifadianPay;
import com.meteor.aifadianpay.afdian.response.Order;
import com.meteor.aifadianpay.filter.OrderFilter;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 检查未填写备注订单
 */
public class CheckRemarkFilter implements OrderFilter {
    @Override
    public List<Order> meet(List<Order> orders) {
        return orders.stream().filter(order -> {
            if(order.getRemark()==null){
                if(AifadianPay.debug) {
                    AifadianPay.INSTANCE.getLogger().info("订单"+order.getOutTradeNo()+"未正常处理,原因: 未留言玩家id");
                }
            }
            return order.getRemark()!=null;
        }).collect(Collectors.toList());
    }
}
