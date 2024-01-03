package com.meteor.aifadianpay.command.sub;

import com.meteor.aifadianpay.AifadianPay;
import com.meteor.aifadianpay.afdian.AfadianApi;
import com.meteor.aifadianpay.afdian.request.AfdOrderReq;
import com.meteor.aifadianpay.afdian.response.Order;
import com.meteor.aifadianpay.afdian.response.Orders;
import com.meteor.aifadianpay.afdian.response.QueryOrderResponse;
import com.meteor.aifadianpay.command.SubCmd;
import com.meteor.aifadianpay.httputil.callback.AsyncHttpResponseCallBack;
import com.meteor.aifadianpay.httputil.response.PackHttpResponse;
import com.meteor.aifadianpay.util.BaseConfig;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InfoCmd extends SubCmd {
    public InfoCmd(JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    public String label() {
        return "info";
    }

    @Override
    public String getPermission() {
        return "afadian.admin";
    }

    @Override
    public boolean playersOnly() {
        return false;
    }

    @Override
    public String usage() {
        return null;
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
                    Map<String,String> params = new HashMap<>();
                    params.put("@trade_no@",order.getOutTradeNo());
                    params.put("@state@", AifadianPay.INSTANCE.getiStorage().isHandleOrder(order.getOutTradeNo())
                    ?"§a已处理":"§c未处理");
                    params.put("@remark@",order.getRemark()==null?"§c未留言":order.getRemark());
                    params.put("@plan@",order.getPlanTitle());
                    params.put("@price@",order.getTotalAmount());

                    BaseConfig.STORE.getMessageBox().getMessageList(params,"message.order-info.state")
                            .forEach(s->p0.sendMessage(s));
                }
            }

            @Override
            public void fail(Exception e) {

            }
        });
    }
}
