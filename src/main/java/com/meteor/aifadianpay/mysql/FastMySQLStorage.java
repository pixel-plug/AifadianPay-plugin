package com.meteor.aifadianpay.mysql;



import com.meteor.aifadianpay.mysql.column.Column;
import com.meteor.aifadianpay.mysql.data.KeyValue;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.*;
import java.util.*;

public class FastMySQLStorage implements IStorage {

    private JavaPlugin plugin;
    private String ip;
    private int port;
    private String param;
    private String user;
    private String password;
    private String database;
    private Connection connection;
    private ConfigurationSection section;
    private BukkitRunnable check;

    public FastMySQLStorage(JavaPlugin plugin, ConfigurationSection section) {
        this(plugin,
                section.getString("ip"),
                section.getInt("port", 3306),
                section.getString("param", ""),
                section.getString("user", "root"),
                section.getString("password", "root"),
                section.getString("database"),
                section);
    }

    private FastMySQLStorage(JavaPlugin plugin, String ip, int port, String param, String user, String password, String database, ConfigurationSection section) {
        this.plugin = plugin;
        this.ip = ip;
        this.port = port;
        this.param = param;
        this.user = user;
        this.password = password;
        this.database = database;
        this.section = section;
    }

    /**
     * 开启数据库
     */
    @Override
    public void enable() {
        try {
            connect();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            plugin.getLogger().info("数据库初次连接失败,请检查配置文件");
            plugin.getServer().getPluginManager().disablePlugin(plugin);
            return;
        }
        check = new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    execute(section.getString("validSql"));
                } catch (Throwable throwable) {
                    try {
                        connect();
                    } catch (Throwable e) {
                        cancel();
                        plugin.getLogger().info("数据库断开连接无法重连！");
                    }
                }
            }
        };
        check.runTaskTimerAsynchronously(plugin, 0L, section.getLong("checkTime", 600L) * 20L);
    }

    /**
     * 执行sql命令
     *
     * @param sql sql命令
     */
    private void execute(String sql) {
        try {
            PreparedStatement pst = getConnection().prepareStatement(sql);
            pst.execute();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    /**
     * 关闭数据库
     */
    @Override
    public void disable() {
        try {
            close();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            check.cancel();
        }
    }

    private void close() throws Throwable {
        try {
            if (this.connection != null && !this.connection.isClosed()) {
                this.connection.close();
            }
        } catch (SQLException e) {
            throw new Throwable("数据库关闭失败", e);
        }
    }

    /**
     * 创建表
     *
     * @param name    名字
     * @param columns 字段
     */
    public void createTable(String name, Column... columns) {
        PreparedStatement pst = null;
        try {
            StringBuilder sb = new StringBuilder("CREATE TABLE IF NOT EXISTS `" + name + "` (");
            for (int i = 0; i < columns.length; i++) {
                Column column = columns[i];
                sb.append(column.getName()).append(" ").append(column.getType());
                if (Column.hasBracket(column.getType())) {
                    sb.append("(").append(column.toStringM()).append(")");
                }
                if (column.isPrimary()) {
                    sb.append(" PRIMARY KEY");
                }
                if (i == (columns.length - 1)) {
                    sb.append(")");
                } else {
                    sb.append(", ");
                }
            }
            plugin.getLogger().info(sb.toString());
            pst = getConnection().prepareStatement(sb.toString());
            pst.executeUpdate();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            free(pst);
        }
    }

    /**
     * 是否存在
     *
     * @param table       表
     * @param keyColumn   判断字段
     * @param searchValue 索引
     * @param <K>         索引泛型
     * @return 是否存在
     */
    public <K> boolean isExists(String table, String keyColumn, K searchValue) {
        StringBuilder sb = new StringBuilder("SELECT `" + keyColumn + "` FROM `" + table + "` WHERE " + keyColumn + "=?");
        PreparedStatement pst = null;
        ResultSet set = null;
        try {
            pst = getConnection().prepareStatement(sb.toString());
            pst.setObject(1, searchValue);
            set = pst.executeQuery();
            if (set.next()) {
                return true;
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            free(set, pst);
        }
        return false;
    }

    /**
     * 删除数据
     *
     * @param table     表
     * @param keyColumn 索引字段
     * @param key       索引
     * @param <K>       索引泛型
     */
    public <K> void delete(String table, String keyColumn, K key) {
        StringBuilder sb = new StringBuilder("DELETE FROM `" + table + "` WHERE " + keyColumn + "=?");
        PreparedStatement pst = null;
        try {
            pst = getConnection().prepareStatement(sb.toString());
            pst.setObject(1, key);
            pst.executeUpdate();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            free(pst);
        }
    }

    /**
     * 获取数据
     *
     * @param table       表
     * @param keyColumn   查询字段
     * @param searchValue 查询值
     * @param column      字段
     * @param <K>         索引泛型
     * @param <V>         返回值泛型
     * @return 数据
     */
    public <K, V> V get(String table, String keyColumn, K searchValue, String column) {
        V value = null;
        StringBuilder sb = new StringBuilder("SELECT `" + column + "` FROM `" + table + "` WHERE " + keyColumn + "=?");
        ResultSet set = null;
        PreparedStatement pst = null;
        try {
            pst = getConnection().prepareStatement(sb.toString());
            pst.setObject(1, searchValue);
            set = pst.executeQuery();
            if (set.next()) {
                Object object = set.getObject(column);
                value = (V) object;
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            free(set, pst);
        }
        return value;
    }

    /**
     * 获取数据
     *
     * @param table       表
     * @param keyColumn   查询字段
     * @param searchValue 查询值
     * @param column      字段
     * @param <K>         索引泛型
     * @param <V>         返回值泛型
     * @return 数据
     */
    public <K, V> V getOrDefault(String table, String keyColumn, K searchValue, String column, V defaultValue) {
        V value = null;
        StringBuilder sb = new StringBuilder("SELECT `" + column + "` FROM `" + table + "` WHERE " + keyColumn + "=?");
        ResultSet set = null;
        PreparedStatement pst = null;
        try {
            pst = getConnection().prepareStatement(sb.toString());
            pst.setObject(1, searchValue);
            set = pst.executeQuery();
            if (set.next()) {
                Object object = set.getObject(column);
                value = (V) object;
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            free(set, pst);
        }
        if (value == null) {
            value = defaultValue;
        }
        return value;
    }

    /**
     * 获取数据
     *
     * @param table        表
     * @param keyColumn    查询字段
     * @param searchValue  查询值
     * @param valuesColumn 值字段
     * @param <K>          索引泛型
     * @return 数据Map
     */
    public <K> Map<String, Object> get(String table, String keyColumn, K searchValue, String... valuesColumn) {
        Map<String, Object> map = new LinkedHashMap<>();
        StringBuilder sb = new StringBuilder("SELECT " + getArrayString(valuesColumn) + " FROM `" + table + "` WHERE " + keyColumn + "=?");
        ResultSet set = null;
        PreparedStatement pst = null;
        try {
            pst = getConnection().prepareStatement(sb.toString());
            pst.setObject(1, searchValue);
            set = pst.executeQuery();
            if (set.next()) {
                for (String s : valuesColumn) {
                    map.put(s, set.getObject(s));
                }
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            free(set, pst);
        }
        return map;
    }

    /**
     * 获取数据List
     *
     * @param table       表
     * @param keyColumn   查询字段
     * @param searchValue 查询值
     * @param valueColumn 值字段
     * @param <K>         索引泛型
     * @return 数据Map
     */
    public <K, V> List<V> getList(String table, String keyColumn, K searchValue, String valueColumn) {
        List<V> list = new ArrayList<>();
        StringBuilder sb = new StringBuilder("SELECT " + valueColumn + " FROM `" + table + "` WHERE " + keyColumn + "=?");
        ResultSet set = null;
        PreparedStatement pst = null;
        try {
            pst = getConnection().prepareStatement(sb.toString());
            pst.setObject(1, searchValue);
            set = pst.executeQuery();
            if (set.next()) {
                list.add((V) set.getObject(valueColumn));
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            free(set, pst);
        }
        return list;
    }

    /**
     * 获取数据List
     *
     * @param table       表
     * @param keyColumn   查询字段
     * @param searchValue 查询值
     * @param valueColumn 值字段
     * @param <K>         索引泛型
     * @return 数据Map
     */
    public <K, V> List<V> getListOrDefault(String table, String keyColumn, K searchValue, String valueColumn, V defaultValue) {
        List<V> list = new ArrayList<>();
        StringBuilder sb = new StringBuilder("SELECT " + valueColumn + " FROM `" + table + "` WHERE " + keyColumn + "=?");
        ResultSet set = null;
        PreparedStatement pst = null;
        try {
            pst = getConnection().prepareStatement(sb.toString());
            pst.setObject(1, searchValue);
            set = pst.executeQuery();
            if (set.next()) {
                V object = (V) set.getObject(valueColumn);
                if (object == null) {
                    object = defaultValue;
                }
                list.add(object);
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            free(set, pst);
        }
        return list;
    }

    /**
     * 获取数据List
     *
     * @param table        表
     * @param keyColumn    查询字段
     * @param searchValue  查询值
     * @param valuesColumn 值字段
     * @param <K>          索引泛型
     * @return 数据Map
     */
    public <K> List<Map<String, Object>> getList(String table, String keyColumn, K searchValue, String... valuesColumn) {
        List<Map<String, Object>> list = new ArrayList<>();
        StringBuilder sb = new StringBuilder("SELECT " + getArrayString(valuesColumn) + " FROM `" + table + "` WHERE " + keyColumn + "=?");
        ResultSet set = null;
        PreparedStatement pst = null;
        try {
            pst = getConnection().prepareStatement(sb.toString());
            pst.setObject(1, searchValue);
            set = pst.executeQuery();
            if (set.next()) {
                Map<String, Object> map = new HashMap<>();
                for (String s : valuesColumn) {
                    map.put(s, set.getObject(s));
                }
                list.add(map);
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            free(set, pst);
        }
        return list;
    }

    /**
     * 获取所有数据并排序(过时命名)
     *
     * @param table        表
     * @param sortColumn   排序字段
     * @param size         返回数量
     * @param description         是否倒序
     * @param valuesColumn 值字段
     * @return List<数据Map>
     */
    @Deprecated
    public List<Map<String, Object>> getList(String table, String sortColumn, int size, boolean description, String... valuesColumn) {
        List<Map<String, Object>> list = new ArrayList<>();
        StringBuilder sb = new StringBuilder("SELECT " + getArrayString(valuesColumn) + " FROM `" + table + "` ORDER BY " + sortColumn).append(description ? " description" : "").append((size < 0 ? "" : " LIMIT " + size));
        ResultSet set = null;
        PreparedStatement pst = null;
        try {
            pst = getConnection().prepareStatement(sb.toString());
            set = pst.executeQuery();
            while (set.next()) {
                Map<String, Object> map = new LinkedHashMap<>();
                for (String s : valuesColumn) {
                    map.put(s, set.getObject(s));
                }
                list.add(map);
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            free(set, pst);
        }
        return list;
    }

    /**
     * 获取所有数据并排序
     *
     * @param table        表
     * @param sortColumn   排序字段
     * @param size         返回数量
     * @param description         是否倒序
     * @param valuesColumn 值字段
     * @return List<数据Map>
     */
    public List<Map<String, Object>> getSortList(String table, String sortColumn, int size, boolean description, String... valuesColumn) {
        List<Map<String, Object>> list = new ArrayList<>();
        StringBuilder sb = new StringBuilder("SELECT " + getArrayString(valuesColumn) + " FROM `" + table + "` ORDER BY " + sortColumn).append(description ? " description" : "").append((size < 0 ? "" : " LIMIT " + size));
        ResultSet set = null;
        PreparedStatement pst = null;
        try {
            pst = getConnection().prepareStatement(sb.toString());
            set = pst.executeQuery();
            while (set.next()) {
                Map<String, Object> map = new LinkedHashMap<>();
                for (String s : valuesColumn) {
                    map.put(s, set.getObject(s));
                }
                list.add(map);
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            free(set, pst);
        }
        return list;
    }

    /**
     * 插入
     *
     * @param table 表
     * @param kv    键值
     */
    public void put(String table, KeyValue... kv) {
        StringBuilder sb = new StringBuilder("INSERT INTO `" + table + "` (" + getArrayString(kv) + ") VALUES(" + getArrayString(kv.length) + ") ON DUPLICATE KEY UPDATE " + getArrayStringValues(kv));
        PreparedStatement pst = null;
        try {
            pst = getConnection().prepareStatement(sb.toString());
            for (int i = 0; i < kv.length; i++) {
                pst.setObject(i + 1, kv[i].getValue());
            }
            pst.executeUpdate();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            free(pst);
        }
    }

    private String getArrayString(String... array) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            if (i == (array.length - 1)) {
                sb.append(array[i]);
            } else {
                sb.append(array[i]).append(", ");
            }
        }
        return sb.toString();
    }

    private String getArrayString(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            if (i == (length - 1)) {
                sb.append("?");
            } else {
                sb.append("?").append(", ");
            }
        }
        return sb.toString();
    }

    private String getArrayString(KeyValue... kv) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < kv.length; i++) {
            KeyValue keyValue = kv[i];
            if (i == (kv.length - 1)) {
                sb.append(keyValue.getKey());
            } else {
                sb.append(keyValue.getKey()).append(", ");
            }
        }
        return sb.toString();
    }

    private String getArrayStringValues(KeyValue... kv) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < kv.length; ++i) {
            KeyValue keyValue = kv[i];
            if (i == kv.length - 1) {
                sb.append(keyValue.getKey()).append("=VALUES(").append(keyValue.getKey()).append(")");
            } else {
                sb.append(keyValue.getKey()).append("=VALUES(").append(keyValue.getKey()).append(")").append(", ");
            }
        }
        return sb.toString();
    }

    public void free(PreparedStatement statement) {
        try {
            if (statement != null && !statement.isClosed()) {
                statement.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void free(ResultSet set, PreparedStatement statement) {
        try {
            if (set != null && !set.isClosed()) {
                set.close();
            }
            if (statement != null && !statement.isClosed()) {
                statement.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取连接
     *
     * @return 连接
     * @throws Throwable 连接异常
     */
    public Connection getConnection() throws Throwable {
        try {
            if (connection == null || connection.isClosed()) {
                connect();
            }
            return connection;
        } catch (Throwable e) {
            throw new Throwable("Connection is null or close", e);
        }
    }

    private void connect() throws Throwable {
        String url = "jdbc:mysql://" + ip + ":" + port + "/" + database;
        try {
            this.connection = DriverManager.getConnection(param.isEmpty() ? url : url + "?" + param, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
            plugin.getLogger().info("数据库链接失败");
            throw new Throwable("数据库连接失败", e);
        }
    }
}
