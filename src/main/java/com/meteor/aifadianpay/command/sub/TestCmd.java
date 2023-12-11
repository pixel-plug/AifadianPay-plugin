package com.meteor.aifadianpay.command.sub;

import com.meteor.aifadianpay.command.SubCmd;
import com.meteor.aifadianpay.data.job.QueryOrderJob;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.quartz.JobExecutionException;

public class TestCmd extends SubCmd {
    public TestCmd(JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    public String label() {
        return "test";
    }

    @Override
    public String getPermission() {
        return "afidian.admin";
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
        QueryOrderJob job = new QueryOrderJob();
        try {
            job.execute(null);
        } catch (JobExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
