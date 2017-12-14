package com.fangjian.study.annotation.construc;

import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @description:
 * @author: fangjian<jian.fang@chinaredstar.com>
 * @date: Create in 16:20 2017/12/13
 * @modified by:
 */
//@Configuration
public class ConstrucService {
    SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss.SSS");//设置日期格式,精确到毫秒

    //被@PostConstruct修饰的方法会在服务器加载Servlet的时候运行，并且只会被服务器调用一次，类似于Serclet的inti()方法。被@PostConstruct修饰的方法会在构造函数之后，init()方法之前运行
    @PostConstruct
    public void someMethod() {
        System.out.println("时间：" + df.format(new Date()) + "执行@PostConstruct修饰的someMethod()方法...");
    }


    //被@PreDestroy修饰的方法会在服务器卸载Servlet的时候运行，并且只会被服务器调用一次，类似于Servlet的destroy()方法。被@PreDestroy修饰的方法会在destroy()方法之后运行，在Servlet被彻底卸载之前
    @PreDestroy
    public void otherMethod() {
        System.out.println("时间：" + df.format(new Date()) + "执行@PreDestroy修饰的otherMethod()方法...");
    }

}
