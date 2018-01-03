package com.fangjian.framework.mq.rabbitmq.config;

import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.concurrent.Executors;


/**
 * @description:
 * @author: fangjian<jian.fang@chinaredstar.com>
 * @date: Create in 16:22 2017/12/21
 * @modified by:
 */
@Configuration
public class RabbitmqConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitmqConfig.class);


    @Value("${rabbitmq.host}")
    private String rabbitmqHost;
    @Value("${rabbitmq.port}")
    private Integer rabbitmqPort;
    @Value("${rabbitmq.username}")
    private String rabbitmqUsername;
    @Value("${rabbitmq.password}")
    private String rabbitmqPassword;
    @Value("${rabbitmq.vhost}")
    private String rabbitmqVirtualHost;

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(rabbitmqHost, this.rabbitmqPort);
        connectionFactory.setUsername(this.rabbitmqUsername);
        connectionFactory.setPassword(this.rabbitmqPassword);
        connectionFactory.setVirtualHost(this.rabbitmqVirtualHost);
        /** 如果要进行消息回调，则这里必须要设置为true */
        connectionFactory.setPublisherConfirms(true);
        return connectionFactory;
    }

    @Bean//RabbitTemplate，用来发送消息。
    /** 因为要设置回调类，所以应是prototype类型，如果是singleton类型，则回调类为最后一次设置 */
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public RabbitTemplate rabbitTemplateBean() {
        RabbitTemplate template = new RabbitTemplate(connectionFactory());
        template.setMessageConverter(new Jackson2JsonMessageConverter());
        return template;
    }

    @Bean
    public RabbitAdmin rabbitAdmin() {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory());

        return rabbitAdmin;
    }


    /**
     * rabbit client 链接工厂
     *
     * @return
     */
    @Bean
    public com.rabbitmq.client.ConnectionFactory clientConnectionFactory() {
        com.rabbitmq.client.ConnectionFactory connectionFactory = new com.rabbitmq.client.ConnectionFactory();
        connectionFactory.setHost(this.rabbitmqHost);
        connectionFactory.setPort(this.rabbitmqPort);
        connectionFactory.setUsername(this.rabbitmqUsername);
        connectionFactory.setPassword(this.rabbitmqPassword);
        connectionFactory.setVirtualHost(this.rabbitmqVirtualHost);

        return connectionFactory;
    }



/*
//    @Bean//DirectExchange
//    public DirectExchange defaultExchange() {
//        return new DirectExchange("direct-01");
//    }

//    @Bean//DirectExchange
//    public TopicExchange defaultExchange() {
//        return new TopicExchange(EXCHANGE_NAME);
//    }

    //    @Bean//Queue，构建队列，名称，是否持久化之类
    public Queue queue() {
        return new Queue(QUEUE_NAME, true);
    }

//    @Bean//Binding，将DirectExchange与Queue进行绑定
//    public Binding binding() {
//        return BindingBuilder.bind(queue()).to(defaultExchange()).with(ROUTING_KEY);
//    }


    @Bean//SimpleMessageListenerContainer，消费者
    public SimpleMessageListenerContainer messageContainer() {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory());
        container.setQueues(queue());
        container.setExposeListenerChannel(true);
        container.setMaxConcurrentConsumers(1);
        container.setConcurrentConsumers(1);
        container.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        container.setMessageListener(new ChannelAwareMessageListener() {

            public void onMessage(Message message, com.rabbitmq.client.Channel channel) throws Exception {
                byte[] body = message.getBody();
                LOGGER.info("消费端接收到消息:[{}]", new String(body));
                if (1 == 1) {
                    throw new RuntimeException("处理失败1");
                }
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            }
        });
        return container;
    }*/


}
