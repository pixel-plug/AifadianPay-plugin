package com.meteor.aifadianpay.command.sub;

import com.meteor.aifadianpay.AifadianPay;
import com.meteor.aifadianpay.command.SubCmd;
import com.meteor.aifadianpay.data.job.QueryOrderJob;
import com.meteor.aifadianpay.util.BaseConfig;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.quartz.JobExecutionException;

import java.util.HashMap;
import java.util.Map;

public class DebugCmd extends SubCmd {
    public DebugCmd(JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    public String label() {
        return "debug";
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
        return "开始调试模式";
    }

    @Override
    public void perform(CommandSender p0, String[] p1) {
        AifadianPay.debug = !AifadianPay.debug;
        Map<String,String> params = new HashMap<>();
        params.put("@mode@",String.valueOf(AifadianPay.debug));
        p0.sendMessage(BaseConfig.STORE.getMessageBox().getMessage(params,"message.debug"));
        QueryOrderJob job = new QueryOrderJob();
        try {
            job.execute(false);
        } catch (JobExecutionException e) {
            throw new RuntimeException(e);
        }

    }
}
