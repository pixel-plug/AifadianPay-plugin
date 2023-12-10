package com.meteor.aifadianpay.filter.sub;

import com.meteor.aifadianpay.AifadianPay;
import com.meteor.aifadianpay.afdian.response.Order;
import com.meteor.aifadianpay.afdian.response.SkuDetail;
import com.meteor.aifadianpay.data.ShopItem;
import com.meteor.aifadianpay.filter.OrderFilter;
import com.meteor.aifadianpay.util.BaseConfig;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 检查商品是否存在
 */
public class CheckShopItemExistFilter implements OrderFilter {
    @Override
    public List<Order> meet(List<Order> orders) {
        return orders.stream().filter(order -> {
            String planTitle = order.getPlanTitle();
            if(!BaseConfig.STORE.getShopItemMap().containsKey(planTitle)) {
                if(AifadianPay.debug){
                    AifadianPay.INSTANCE.getLogger().info("订单"+order.getOutTradeNo()+"未正常处理,原因: 商品 "+planTitle +" 不存在");
                }
                return false;
            }
            ShopItem shopItem = BaseConfig.STORE.getShopItemMap().get(planTitle);
            // 检查型号存在
            for (SkuDetail skuDetail : order.getSkuDetail()) {
                if(!shopItem.getSkuDetailMap().containsKey(skuDetail.getName())) {
                    if(AifadianPay.debug){
                        AifadianPay.INSTANCE.getLogger().info("订单发货错误"+order.getOutTradeNo());
                        AifadianPay.INSTANCE.getLogger().info("商品 "+shopItem.getPlanTitle()+" 型号 -> "+skuDetail.getName()+" 不存在");
                    }
                    return false;
                }
            }
            return true;
        }).collect(Collectors.toList());
    }
}
