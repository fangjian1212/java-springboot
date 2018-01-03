package com.fangjian.xj.web.controller.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @description:
 * @author: fangjian<jian.fang@chinaredstar.com>
 * @date: Create in 14:12 2017/12/25
 * @modified by:
 */

@Component
//@RabbitListener(queues = "jc_sms_01")
public class RabbitmqConsumer1 {

    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitmqConsumer1.class);


    @RabbitHandler
    public void process(Message message, com.rabbitmq.client.Channel channel) throws IOException {
        byte[] body = message.getBody();
        LOGGER.info("接收消息：[{}]", new String(body));
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }


}
