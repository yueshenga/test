package com.example.demo.config;

import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//创建Quartz的具体配置类，包含任务绑定和触发器
@Configuration
public class QuartzConfig {

    //.storeDurably():生命当任务未执行会被框架给持久化，不会直接结束
    //.newJob():绑定具体的任务类
    @Bean
    public JobDetail Print(){
        return JobBuilder.newJob(MyQuartz.class).storeDurably().build();
    }

    //定义触发器
//    forjob():绑定跟要执行任务绑定的返回值为JObDetail的方法（即工作明细对象）
//    TriggerBuilder.newTrigger()：创建触发器对象
//    withSchedule(scheduleBuilder)：使用调度器，规定任务的执行策略
    @Bean
    public Trigger PrintJobTrigger(){
        //这里使用cron表达式：
        //"0/5 * * * * ?"
        //意思是：每五秒 任意分 任意小时 任意日 任意月 任意周几
        ScheduleBuilder<CronTrigger> scheduleBuilder = CronScheduleBuilder.cronSchedule("0/5 * * * * ?");
        return TriggerBuilder.newTrigger().forJob(Print()).withSchedule(scheduleBuilder).build();
    }
}

