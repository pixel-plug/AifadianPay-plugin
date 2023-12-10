package com.meteor.aifadianpay.mysql.data;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class BaseDataReading implements IData {

    private JavaPlugin plugin;
    private volatile boolean read = false;
    private volatile boolean reading = false;
    private volatile boolean saving = false;

    public BaseDataReading(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void read(Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            reading = true;
            readData();
            read = true;
            reading = false;
            runnable.run();
        });
    }

    @Override
    public void save(Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            saving = true;
            saveData();
            saving = false;
            runnable.run();
        });
    }

    abstract void readData();

    abstract void saveData();

    @Override
    public boolean isRead() {
        return read;
    }

    public boolean isReading() {
        return reading;
    }

    public boolean isSaving() {
        return saving;
    }
}
