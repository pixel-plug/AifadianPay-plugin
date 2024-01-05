package com.meteor.aifadianpay.command.sub;

import com.meteor.aifadianpay.AifadianPay;
import com.meteor.aifadianpay.command.SubCmd;
import com.meteor.aifadianpay.util.BaseConfig;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class ImportCmd extends SubCmd {
    public ImportCmd(JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    public String label() {
        return "import";
    }

    @Override
    public String getPermission() {
        return "afdian.admin";
    }

    @Override
    public boolean playersOnly() {
        return false;
    }

    @Override
    public String usage() {
        return "导入数据";
    }

    @Override
    public void perform(CommandSender p0, String[] p1) {
        if(!(p0 instanceof ConsoleCommandSender)) {
            p0.sendMessage(BaseConfig.STORE.getMessageBox().getMessage(null,"message.only-console"));
            return;
        }
        if(p1.length<2) return;
        AifadianPay.INSTANCE.getiStorage().Import(p1[1]);
    }
}
