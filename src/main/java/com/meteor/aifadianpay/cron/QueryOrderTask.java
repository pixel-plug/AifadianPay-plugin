package com.meteor.aifadianpay.cron;

import com.meteor.aifadianpay.AifadianPay;
import com.meteor.aifadianpay.afdian.handle.HandlerQueryOrdersResponse;
import com.meteor.aifadianpay.data.job.QueryOrderJob;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;

import java.util.Set;

/**
 * 轮询任务
 */
public class QueryOrderTask {

    private static Scheduler scheduler = null;

    public static Scheduler getScheduler() {
        return scheduler;
    }

    static {
        try {
            scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }

    private AifadianPay plugin;


    public QueryOrderTask(AifadianPay plugin){
        this.plugin = plugin;
        QueryOrderJob.init(new HandlerQueryOrdersResponse(plugin.getiStorage()));
        this.loadScheduler();
    }

    public Set<JobKey> getJobKeys(){
        try {
            Set<JobKey> boss = scheduler.getJobKeys(GroupMatcher.jobGroupEquals("aifadian"));
            return boss;
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadScheduler(){
        for (JobKey jobKey : getJobKeys()) {
            try {
                scheduler.deleteJob(jobKey);
            } catch (SchedulerException e) {
                throw new RuntimeException(e);
            }
        }

        plugin.reloadConfig();

        String cron = plugin.getConfig().getString("query-cron");
        JobDataMap jobDataMap = new JobDataMap();
        JobDetail bossJob = JobBuilder.newJob(QueryOrderJob.class).withIdentity("aifadian", "aifadian").usingJobData(jobDataMap).build();
        CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity("aifadian","aifadian").withSchedule(
                CronScheduleBuilder.cronSchedule(cron)
        ).build();

        try {
            scheduler.scheduleJob(bossJob,trigger);
            plugin.getLogger().info("已初始化订单查询任务");
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }

    }

}
