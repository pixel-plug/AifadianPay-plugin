package com.meteor.aifadianpay.api.event;

import com.meteor.aifadianpay.afdian.response.Order;
import com.meteor.aifadianpay.afdian.response.SkuDetail;
import com.meteor.aifadianpay.data.ShopItem;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import java.util.List;

/**
 * 正确发货事件
 */
public class SendOutGoodsEvent extends Event {


    private Order order;
    private ShopItem shopItem;
    private String tradeNo;
    private Player player;
    private List<SkuDetail> skuDetails;

    public SendOutGoodsEvent(Player p, ShopItem shopItem, String tradeNo, List<SkuDetail> skuDetails,Order order) {
        this.player = p;
        this.tradeNo = tradeNo;
        this.shopItem = shopItem;
        this.order = order;
        this.skuDetails = skuDetails;
    }

    public Player getPlayer() {
        return player;
    }

    public Order getOrder() {
        return order;
    }

    /**
     * 获取发货商品
     * @return
     */
    public ShopItem getShopItem() {
        return shopItem;
    }

    /**
     * 获取购买型号
     * 注意,这里的SkuDetail并不是ShopItem实例产生的对象
     * getDisplayName(),getRewards()会失败
     * @return
     */
    public List<SkuDetail> getSkuDetails() {
        return skuDetails;
    }

    /**
     * 获取商品编号 (存储的唯一索引)
     * @return
     */
    public String getTradeNo() {
        return tradeNo;
    }

    private static  final HandlerList handlerList = new HandlerList();
    public static HandlerList getHandlerList() {
        return handlerList;
    }


    @Override
    public HandlerList getHandlers() {
        return getHandlerList();
    }
}
