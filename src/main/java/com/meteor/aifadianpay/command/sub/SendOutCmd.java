package com.meteor.aifadianpay.command.sub;

import com.meteor.aifadianpay.AifadianPay;
import com.meteor.aifadianpay.afdian.AfadianApi;
import com.meteor.aifadianpay.afdian.request.AfdOrderReq;
import com.meteor.aifadianpay.afdian.response.Order;
import com.meteor.aifadianpay.afdian.response.QueryOrderResponse;
import com.meteor.aifadianpay.command.SubCmd;
import com.meteor.aifadianpay.httputil.callback.AsyncHttpResponseCallBack;
import com.meteor.aifadianpay.httputil.response.PackHttpResponse;
import com.meteor.aifadianpay.util.BaseConfig;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 手动发货订单
 */
public class SendOutCmd extends SubCmd {
    public SendOutCmd(JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    public String label() {
        return "so";
    }

    @Override
    public String getPermission() {
        return "afadian.use.so";
    }

    @Override
    public boolean playersOnly() {
        return false;
    }

    @Override
    public String usage() {
        return "发货/收货";
    }

    @Override
    public void perform(CommandSender p0, String[] p1) {

        if(p1.length<2) return;

        AfadianApi.afadianApi.queryOrders(new AfdOrderReq(p1[1]), new AsyncHttpResponseCallBack() {
            @Override
            public void success(PackHttpResponse packHttpResponse) {
                QueryOrderResponse queryOrderResponse = AfadianApi.afadianApi.toOrders(packHttpResponse);
                List<Order> orderList = queryOrderResponse.getOrders().getOrderList();
                if(orderList.isEmpty()){
                    p0.sendMessage(BaseConfig.STORE.getMessageBox().getMessage(null,"message.order-info.notExistOrder"));
                }else {
                    Order order = orderList.get(0);
                    if(p0 instanceof Player){
                        Player player = (Player) p0;
                        if(!player.isOp() && (order.getRemark()==null|| !order.getRemark().equalsIgnoreCase(player.getName()))){
                            player.sendMessage(BaseConfig.STORE.getMessageBox().getMessage(null,"message.send-out.remark-error"));
                            return;
                        }
                    }

                    if(AifadianPay.INSTANCE.getiStorage().isHandleOrder(order.getOutTradeNo())){
                        p0.sendMessage(BaseConfig.STORE.getMessageBox().getMessage(null,"message.send-out.repeat"));
                    }else {
                        if(order.getRemark()==null|| Bukkit.getPlayerExact(order.getRemark())==null){
                            p0.sendMessage(BaseConfig.STORE.getMessageBox().getMessage(null,"message.send-out.not-online"));
                            return;
                        }
                        if(AifadianPay.INSTANCE.getiStorage().handeOrder(order,true)){
                            p0.sendMessage(BaseConfig.STORE.getMessageBox().getMessage(null,"message.send-out.success"));
                        }else {
                            p0.sendMessage(BaseConfig.STORE.getMessageBox().getMessage(null,"message.send-out.fail"));
                        }
                    }



                }
            }

            @Override
            public void fail(Exception e) {
                e.printStackTrace();
            }
        });
    }
}
