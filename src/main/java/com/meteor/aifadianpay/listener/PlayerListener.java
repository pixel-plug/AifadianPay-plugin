package com.meteor.aifadianpay.listener;

import com.meteor.aifadianpay.AifadianPay;
import com.meteor.aifadianpay.afdian.response.Order;
import com.meteor.aifadianpay.afdian.response.SkuDetail;
import com.meteor.aifadianpay.api.event.SendOutGoodsEvent;
import com.meteor.aifadianpay.data.ShopItem;
import com.meteor.aifadianpay.util.BaseConfig;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerListener implements Listener {
    private AifadianPay plugin;

    public PlayerListener(AifadianPay plugin){
        this.plugin = plugin;
    }

    /**
     * 发货
     */
    @EventHandler
    public void onGain(SendOutGoodsEvent sendOutGoodsEvent){
        Player player = sendOutGoodsEvent.getPlayer();
        ShopItem shopItem = sendOutGoodsEvent.getShopItem();
        for (SkuDetail buyI : sendOutGoodsEvent.getSkuDetails()) {
            SkuDetail skuDetail = shopItem.getSkuDetailMap().get(buyI.getName());
            for (int i = 0; i < buyI.getCount(); i++) {
                skuDetail.getRewards().stream().forEach(s->{
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(),s.replace("@p@",player.getName()));
                });
            }

        }
        Order order = sendOutGoodsEvent.getOrder();
        Map<String,String> params = new HashMap<>();
        params.put("@price@",String.valueOf(order.getShowAmount()));
        params.put("@real_price@",String.valueOf(order.getTotalAmount()));
        params.put("@trade_no@",order.getOutTradeNo());
        params.put("@shopitem@",shopItem.getPlanTitle());
        params.put("@amount@",sendOutGoodsEvent.getSkuDetails().size()+"");
        // 消息
        List<String> messageList = BaseConfig.STORE.getMessageBox().getMessageList(params, "message.gain-reward");
        messageList.forEach(s->{
            if(s.equalsIgnoreCase("@format@")){
                Map<String,String> skuPramas = new HashMap<>();
                for (SkuDetail buyI : sendOutGoodsEvent.getSkuDetails()) {
                    skuPramas.put("@sku_detail@",buyI.getName());
                    skuPramas.put("@amount@",String.valueOf(buyI.getCount()));
                    player.sendMessage(BaseConfig.STORE.getMessageBox().getMessage(skuPramas,"message.format.sku-detail"));
                }
                return;
            }
            player.sendMessage(s);
        });

    }


}
