package com.meteor.aifadianpay.command.sub;

import com.meteor.aifadianpay.Status;
import com.meteor.aifadianpay.afdian.AfadianApi;
import com.meteor.aifadianpay.command.SubCmd;
import com.meteor.aifadianpay.util.BaseConfig;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class PingCmd extends SubCmd {
    public PingCmd(JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    public String label() {
        return "ping";
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
        return "测试api状态";
    }

    @Override
    public void perform(CommandSender p0, String[] p1) {
        Status ping = AfadianApi.afadianApi.ping();
        if(ping==Status.N200){
            p0.sendMessage(BaseConfig.STORE.getMessageBox().getMessage(null,"message.ping.success"));
        }else {
            Map<String,String> params = new HashMap<>();
            params.put("@tip@",ping.getTip());
            p0.sendMessage(BaseConfig.STORE.getMessageBox().getMessage(params,"message.ping.fail"));
        }
    }
}
