package com.fangjian.study.annotation.schedule;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @description: 配置类
 * @author: fangjian<jian.fang@chinaredstar.com>
 * @date: Create in 15:14 2017/12/13
 * @modified by:
 */

@Configuration
@ComponentScan("com.fangjian.study.annotation.schedule")
@EnableScheduling //通过@EnableScheduling注解 开启对计划任务的支持
public class TaskScheduleConfig {

}
