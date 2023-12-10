package com.meteor.aifadianpay.command.sub;

import com.meteor.aifadianpay.command.SubCmd;
import com.meteor.aifadianpay.util.BaseConfig;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class HelpCmd extends SubCmd {
    public HelpCmd(JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    public String label() {
        return "help";
    }

    @Override
    public String getPermission() {
        return "afadian.use.help";
    }

    @Override
    public boolean playersOnly() {
        return false;
    }

    @Override
    public String usage() {
        return "查看帮助";
    }

    @Override
    public void perform(CommandSender p0, String[] p1) {
        for (String s : BaseConfig.STORE.getMessageBox().getMessageList(null, "message.help")) {
            p0.sendMessage(s);
        }
    }
}
