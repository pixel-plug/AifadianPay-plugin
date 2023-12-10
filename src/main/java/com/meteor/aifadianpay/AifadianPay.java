package com.meteor.aifadianpay;

import com.meteor.aifadianpay.afdian.AfadianApi;
import com.meteor.aifadianpay.afdian.handle.HandlerQueryOrdersResponse;
import com.meteor.aifadianpay.command.AifadianCommandMain;
import com.meteor.aifadianpay.cron.QueryOrderTask;
import com.meteor.aifadianpay.data.job.QueryOrderJob;
import com.meteor.aifadianpay.listener.PlayerListener;
import com.meteor.aifadianpay.storage.IStorage;
import com.meteor.aifadianpay.storage.sub.MysqlStorage;
import com.meteor.aifadianpay.util.BaseConfig;
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

    public AifadianPay(){
        INSTANCE = this;
    }

    private IStorage iStorage;

    @Override
    public void onEnable() {
        // 初始化配置文件
        BaseConfig.init(this);
        this.initStorage();

        // 初始化爱发电API
        AfadianApi.init(getConfig().getString("user"),getConfig().getString("token"));

        // 初始化计划任务
        queryOrderTask = new QueryOrderTask(this);

        // 注册监听器
        getServer().getPluginManager().registerEvents(new PlayerListener(this),this);

        // 注册指令
        (commandMain = new AifadianCommandMain(this)).init();
        getCommand("apl").setExecutor(commandMain);

        debug = getConfig().getBoolean("debug",false);

        if(AifadianPay.debug) getLogger().info("已开启DEBUG模式，将在控制台输出调试信息");
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
        if(getConfig().isBoolean("mysql-info.enable")) iStorage = new MysqlStorage(this);
    }

    /**
     * 重载时处理存储切换和计划任务
     */
    public void reload(){

        try {
            QueryOrderTask.getScheduler().clear();
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }

        QueryOrderJob.init(new HandlerQueryOrdersResponse(iStorage));
        AfadianApi.init(getConfig().getString("user"),getConfig().getString("token"));
    }

    public IStorage getiStorage() {
        return iStorage;
    }
}
