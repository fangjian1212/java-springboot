package com.fangjian.framework.mq.rabbitmq.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;

/**
 * @description:
 * @author: fangjian<jian.fang@chinaredstar.com>
 * @date: Create in 16:20 2017/12/25
 * @modified by:
 */
public interface IRabbitmqProducerService extends RabbitTemplate.ConfirmCallback {


    /**
     * 消息放入
     *
     * @param message
     * @param exchange
     * @param routingKey
     */
    void send(Object message, String exchange, String routingKey);


}
