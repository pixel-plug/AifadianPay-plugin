package com.meteor.aifadianpay;

import com.meteor.Metrics;
import com.meteor.aifadianpay.afdian.AfadianApi;
import com.meteor.aifadianpay.afdian.handle.HandlerQueryOrdersResponse;
import com.meteor.aifadianpay.command.AifadianCommandMain;
import com.meteor.aifadianpay.cron.QueryOrderTask;
import com.meteor.aifadianpay.data.job.QueryOrderJob;
import com.meteor.aifadianpay.listener.PlayerListener;
import com.meteor.aifadianpay.storage.IStorage;
import com.meteor.aifadianpay.storage.sub.MysqlStorage;
import com.meteor.aifadianpay.storage.sub.SqliteStorage;
import com.meteor.aifadianpay.util.BaseConfig;
import com.meteor.api.hook.PlaceholderHook;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;
public final class AifadianPay extends JavaPlugin {

    public static boolean debug;

    public static AifadianPay INSTANCE;
    public QueryOrderTask queryOrderTask;
    private AifadianCommandMain commandMain;
    private Metrics metrics;

    public AifadianPay(){
        INSTANCE = this;
    }

    private IStorage iStorage;

    @Override
    public void onEnable() {
        // 初始化配置文件
        BaseConfig.init(this);

        try {
            this.initStorage();
        }catch (Exception e){
            getLogger().info("数据库信息错误!");
        }

        AfadianApi.init(getConfig().getString("user"),getConfig().getString("token"));
        AfadianApi.ping(Bukkit.getConsoleSender());
        // 初始化计划任务
        queryOrderTask = new QueryOrderTask(this);

        // 注册监听器
        getServer().getPluginManager().registerEvents(new PlayerListener(this),this);

        // 注册指令
        (commandMain = new AifadianCommandMain(this)).init();
        getCommand("apl").setExecutor(commandMain);

        debug = getConfig().getBoolean("debug",false);

        if(getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")){
            (new PlaceholderHook()).register();
            getLogger().info("已兼容PlaceholderAPI");
        }

        if(AifadianPay.debug) getLogger().info("已开启DEBUG模式，将在控制台输出调试信息");

        metrics = new Metrics(this,19687);

        getLogger().info("插件已加载,感谢使用");
        getLogger().info("使用问题欢迎加群 653440235 反馈");

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        try {
            QueryOrderTask.getScheduler().clear();
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 初始化存储
     */
    private void initStorage(){
        if(getConfig().getBoolean("mysql-info.enable",false))
            this.iStorage = new MysqlStorage(this);
        else this.iStorage = new SqliteStorage(this);
        QueryOrderTask.init(iStorage);
    }


    public IStorage getiStorage() {
        return iStorage;
    }
}
