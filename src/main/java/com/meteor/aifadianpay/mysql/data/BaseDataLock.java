package com.meteor.aifadianpay.mysql.data;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class BaseDataLock implements IData {

    private JavaPlugin plugin;
    private volatile boolean read = false;

    public BaseDataLock(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void read(Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            lock();
            readData();
            read = true;
            unLock();
            runnable.run();
        });
    }

    @Override
    public void save(Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            lock();
            saveData();
            unLock();
            runnable.run();
        });
    }

    abstract void readData();

    abstract void saveData();

    abstract boolean lock();

    abstract boolean unLock();

}
