package com.meteor.aifadianpay.command;

import com.meteor.aifadianpay.AifadianPay;
import com.meteor.aifadianpay.command.sub.HelpCmd;
import com.meteor.aifadianpay.command.sub.PingCmd;
import com.meteor.aifadianpay.command.sub.ReloadCmd;
import org.bukkit.plugin.java.JavaPlugin;

public class AifadianCommandMain extends AbstractCommandManager{
    public AifadianCommandMain(JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    public void init() {
        register(new ReloadCmd(AifadianPay.INSTANCE));
        register(new HelpCmd(AifadianPay.INSTANCE));
        register(new PingCmd(AifadianPay.INSTANCE));
    }
}
