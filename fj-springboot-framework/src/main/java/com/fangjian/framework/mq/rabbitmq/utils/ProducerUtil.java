package com.fangjian.framework.mq.rabbitmq.utils;

import com.fangjian.framework.mq.rabbitmq.service.IRabbitmqProducerService;
import com.fangjian.framework.utils.self.spring.ApplicationContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @description:
 * @author: fangjian<jian.fang@chinaredstar.com>
 * @date: Create in 16:47 2017/12/25
 * @modified by:
 */
public class ProducerUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProducerUtil.class);


    private static IRabbitmqProducerService producer = ApplicationContextUtil.getApplicationContext().getBean(IRabbitmqProducerService.class);

    /**
     * Rabbitmq 发送消息
     *
     * @param message 消息
     * @param queue   queue名字
     */
    public static void send(Object message, String queue) {

        send(message, queue, queue);
    }

    /**
     * Rabbitmq 发送消息
     *
     * @param message    消息
     * @param exchange   exchange
     * @param routingKey routingKey
     */
    public static void send(Object message, String exchange, String routingKey) {

        producer.send(message, exchange, routingKey);
    }


}
