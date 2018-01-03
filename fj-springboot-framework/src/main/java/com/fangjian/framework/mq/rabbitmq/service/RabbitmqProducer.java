package com.fangjian.framework.mq.rabbitmq.service;

import com.fangjian.framework.utils.self.json.JsonUtil;
import com.fangjian.framework.utils.self.json.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * @description:
 * @author: fangjian<jian.fang@chinaredstar.com>
 * @date: Create in 16:20 2017/12/25
 * @modified by:
 */
@Service
public class RabbitmqProducer implements IRabbitmqProducerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitmqProducer.class);

    //    @Resource(name = "rabbitTemplate")
    private RabbitTemplate rabbitTemplate;

    @Autowired
    public RabbitmqProducer(RabbitTemplate rabbitTemplateBean) {
        this.rabbitTemplate = rabbitTemplateBean;
        this.rabbitTemplate.setConfirmCallback(this);
    }


    public void send(Object message, String exchange, String routingKey) {
        LOGGER.info("exchange routingKey message[{},{},{}]", exchange, routingKey, JsonUtil.toString(message));
        //执行保存
        CorrelationData correlationId = new CorrelationData(UUID.randomUUID().toString());
        rabbitTemplate.convertAndSend(exchange, routingKey, message, correlationId);

    }


    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        LOGGER.info("confirm...");
        if (ack) {
            LOGGER.info("[{}]消息发送确认成功", correlationData);
        } else {
            LOGGER.info("[{}]消息发送确认失败:[{}]", correlationData, cause);
        }
    }


}
