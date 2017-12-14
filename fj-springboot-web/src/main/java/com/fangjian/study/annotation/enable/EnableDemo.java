package com.fangjian.study.annotation.enable;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @description:
 * @author: fangjian<jian.fang@chinaredstar.com>
 * @date: Create in 16:58 2017/12/13
 * @modified by:
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = {ElementType.TYPE})
@Documented
@Import({DemoConfig.class})
@Configuration
public @interface EnableDemo {

    String param() default "fangjian";

}
