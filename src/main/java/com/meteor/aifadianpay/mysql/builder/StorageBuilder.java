package com.meteor.aifadianpay.mysql.builder;

import com.meteor.aifadianpay.mysql.FastMySQLStorage;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class StorageBuilder {

    private JavaPlugin plugin;
    private String ip;
    private int port;
    private String param;
    private String user;
    private String password;
    private String database;
    private String validSql;

    public StorageBuilder setPlugin(JavaPlugin plugin) {
        this.plugin = plugin;
        return this;
    }

    public StorageBuilder setIp(String ip) {
        this.ip = ip;
        return this;
    }

    public StorageBuilder setPort(int port) {
        this.port = port;
        return this;
    }

    public StorageBuilder setParam(String param) {
        this.param = param;
        return this;
    }

    public StorageBuilder setUser(String user) {
        this.user = user;
        return this;
    }

    public StorageBuilder setPassword(String password) {
        this.password = password;
        return this;
    }

    public StorageBuilder setDatabase(String database) {
        this.database = database;
//        NBTInjector.
        return this;
    }

    public StorageBuilder setValidSql(String validSql) {
        this.validSql = validSql;
        return this;
    }

    public FastMySQLStorage build() {
        Objects.requireNonNull(plugin, "plugin is null");
        Objects.requireNonNull(ip, "ip is null");
        Objects.requireNonNull(user, "user is null");
        Objects.requireNonNull(password, "password is null");
        Objects.requireNonNull(database, "database is null");
        YamlConfiguration config = new YamlConfiguration();
        config.set("ip", ip);
        config.set("port", port);
        config.set("user", user);
        config.set("password", password);
        config.set("param", param);
        config.set("database", database);
        config.set("validSql", validSql == null ? "select UNIX_TIMESTAMP(NOW())" : validSql);
        return new FastMySQLStorage(plugin, config);
    }

    public StorageBuilder builder() {
        return new StorageBuilder();
    }
}
