package com.meteor.aifadianpay.filter;

import com.meteor.aifadianpay.afdian.response.Order;
import com.meteor.aifadianpay.filter.sub.CheckIsItemFilter;
import com.meteor.aifadianpay.filter.sub.CheckRemarkFilter;
import com.meteor.aifadianpay.filter.sub.CheckShopItemExistFilter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FilterManager {

    private final static List<OrderFilter> filters = new ArrayList<>();

    static{
        filters.add(new CheckShopItemExistFilter());
        filters.add(new CheckRemarkFilter());
        filters.add(new CheckIsItemFilter());
    }

    /**
     * 过滤订单
     * @param orders
     * @return
     */
    public static List<Order> meet(List<Order> orders){


        for (OrderFilter filter : filters) {
            orders = filter.meet(orders);
        }
        return orders;
    }

}
