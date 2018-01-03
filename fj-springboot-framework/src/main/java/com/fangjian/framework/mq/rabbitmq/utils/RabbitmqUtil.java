package com.fangjian.framework.mq.rabbitmq.utils;

import com.fangjian.framework.utils.self.spring.ApplicationContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.util.Assert;

/**
 * @description:
 * @author: fangjian<jian.fang@chinaredstar.com>
 * @date: Create in 10:06 2017/12/26
 * @modified by:
 */
public class RabbitmqUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitmqUtil.class);

    private static final String PREFIX_ROUTING_KEY = "#.";
    private static String DEFAULT_EXCHANGE;

    private RabbitmqUtil() {
    }

    private static final RabbitmqUtil rabbitmqUtil = new RabbitmqUtil();

    public static RabbitmqUtil getRabbitmqUtil(String defaultExchange) {
        Assert.hasText(defaultExchange, "defaultExchange is not null");
        rabbitmqUtil.DEFAULT_EXCHANGE = defaultExchange;
        return rabbitmqUtil;
    }

    private static RabbitAdmin rabbitAdmin = ApplicationContextUtil.getApplicationContext().getBean(RabbitAdmin.class);

    /**
     * 删除 queue
     *
     * @param name queue名字
     * @return
     */
    public void deleteQueue(String name) {
        try {
            rabbitAdmin.deleteQueue(name, false, true);
        } catch (Exception e) {
            LOGGER.error("[{}] is not empty exception:[{}]", name, e);
        }
    }

    /**
     * 删除 exchange
     *
     * @param name exchange名字
     * @return
     */
    public void deleteExchange(String name) {
        try {
            rabbitAdmin.deleteExchange(name);
        } catch (Exception e) {
            LOGGER.error("[{}] deleteExchange exception:[{}]", name, e);
        }
    }

    /**
     * 创建queue 绑定在默认的exchange上 ，并且绑定的routingKey为 (#.name)
     *
     * @param name queue名字
     * @return
     */
    public String declareQueue(String name) {
        return declareQueue(name, false);
    }

    /**
     * 创建queue
     *
     * @param name       queue名字
     * @param ifExchange true 创建新topic exchange(name)，并且绑定的routingKey为 (#.name)
     * @return
     */
    public String declareQueue(String name, boolean ifExchange) {
        String exchange = DEFAULT_EXCHANGE;
        String routingKey = PREFIX_ROUTING_KEY + name;
        if (ifExchange) {
            exchange = name;
            rabbitAdmin.declareExchange(new TopicExchange(exchange));
        }
        String s = rabbitAdmin.declareQueue(new Queue(name));
        rabbitAdmin.declareBinding(new Binding(name, Binding.DestinationType.QUEUE, exchange, routingKey, null));
        return s;
    }

}
