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


    private long lastPingTime;

    /**
     * 是否操作过于频繁
     * @return
     */
    private boolean isPass(){
        return ((System.currentTimeMillis()-lastPingTime)/1000)>=3;
    }

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
        if(isPass()){
            lastPingTime = System.currentTimeMillis();
            AfadianApi.ping(p0);
        }else {
            p0.sendMessage("操作过于频繁");
        }
    }
}
