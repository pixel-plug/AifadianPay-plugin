package com.meteor.aifadianpay.command;

import com.meteor.aifadianpay.AifadianPay;
import com.meteor.aifadianpay.command.sub.*;
import org.bukkit.plugin.java.JavaPlugin;

public class AifadianCommandMain extends AbstractCommandManager{
    public AifadianCommandMain(JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    public void init() {
        register(new ReloadCmd(AifadianPay.INSTANCE));
        register(new TestCmd(AifadianPay.INSTANCE));
        register(new HelpCmd(AifadianPay.INSTANCE));
        register(new PingCmd(AifadianPay.INSTANCE));
        register(new DebugCmd(AifadianPay.INSTANCE));
        register(new InfoCmd(AifadianPay.INSTANCE));
        register(new SendOutCmd(AifadianPay.INSTANCE));
        register(new ExportCmd(AifadianPay.INSTANCE));
        register(new ImportCmd(AifadianPay.INSTANCE));
    }
}
