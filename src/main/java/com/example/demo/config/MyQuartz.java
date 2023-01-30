package com.example.demo.config;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

//定义开发任务的bean，注意，这里要继承QuartzJobBean，
//而不是将定义的开发任务的bean交给Spring去管理
public class MyQuartz extends QuartzJobBean {

    //这个方法里写的逻辑是你需要执行的操作，也就是任务内容
    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
//        System.out.println("MyQuartz定时任务");
    }
}
