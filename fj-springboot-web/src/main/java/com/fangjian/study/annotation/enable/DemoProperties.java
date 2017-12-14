package com.fangjian.study.annotation.enable;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @description:
 * @author: fangjian<jian.fang@chinaredstar.com>
 * @date: Create in 18:37 2017/12/13
 * @modified by:
 */
@ConfigurationProperties(prefix = "springboot.demo")
public class DemoProperties {

    private String name;
    private String select;

    @Override
    public String toString() {
        return "DemoProperties{" +
                "name='" + name + '\'' +
                ", select='" + select + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSelect() {
        return select;
    }

    public void setSelect(String select) {
        this.select = select;
    }
}
