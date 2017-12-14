package com.fangjian.study.annotation.schedule;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @description: 计划任务执行类
 * @author: fangjian<jian.fang@chinaredstar.com>
 * @date: Create in 15:12 2017/12/13
 * @modified by:
 */
@Service
public class ScheduledTaskService {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Scheduled(fixedRate = 5000) //通过@Scheduled声明该方法是计划任务，使用fixedRate属性每隔固定时间执行
    public void reportCurrentTime() {
        System.out.println(Thread.currentThread().getName() + ",每隔5秒执行一次 " + dateFormat.format(new Date()));
    }

    @Scheduled(cron = "0 07 20 ? * *") //使用cron属性可按照指定时间执行，本例指的是每天20点07分执行；
    //cron是UNIX和类UNIX(Linux)系统下的定时任务
    public void fixTimeExecution() {
        System.out.println(Thread.currentThread().getName() + ",在指定时间 " + dateFormat.format(new Date()) + " 执行");
    }

}
