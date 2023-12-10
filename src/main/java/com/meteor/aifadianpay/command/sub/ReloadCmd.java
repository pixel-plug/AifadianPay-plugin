package com.meteor.aifadianpay.command.sub;

import com.meteor.aifadianpay.AifadianPay;
import com.meteor.aifadianpay.afdian.AfadianApi;
import com.meteor.aifadianpay.command.SubCmd;
import com.meteor.aifadianpay.util.BaseConfig;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class ReloadCmd extends SubCmd {
    public ReloadCmd(JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    public String label() {
        return "reload";
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
        return "重载配置文件";
    }

    @Override
    public void perform(CommandSender p0, String[] p1) {
        BaseConfig.STORE.reload();
        AfadianApi.init(plugin.getConfig().getString("user"),plugin.getConfig().getString("token"));
        p0.sendMessage(BaseConfig.STORE.getMessageBox().getMessage(null,"message.reload"));
    }
}
