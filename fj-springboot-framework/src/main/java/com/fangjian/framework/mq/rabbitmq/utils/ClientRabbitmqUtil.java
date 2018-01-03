package com.fangjian.framework.mq.rabbitmq.utils;

import com.fangjian.framework.utils.self.json.JsonUtil;
import com.fangjian.framework.utils.self.spring.ApplicationContextUtil;
import com.rabbitmq.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

/**
 * @description:
 * @author: fangjian<jian.fang@chinaredstar.com>
 * @date: Create in 10:06 2017/12/26
 * @modified by:
 */
public class ClientRabbitmqUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientRabbitmqUtil.class);

    private static final String PREFIX_ROUTING_KEY = "#.";
    private static String DEFAULT_EXCHANGE;

    private ClientRabbitmqUtil() {
    }

    private static final ClientRabbitmqUtil rabbitmqUtil = new ClientRabbitmqUtil();

    public static ClientRabbitmqUtil getRabbitmqUtil(String defaultExchange) {
        Assert.hasText(defaultExchange, "defaultExchange is not null");
        rabbitmqUtil.DEFAULT_EXCHANGE = defaultExchange;
        return rabbitmqUtil;
    }

    private static ConnectionFactory connectionFactory = ApplicationContextUtil.getApplicationContext().getBean(ConnectionFactory.class);


    /**
     * 获取链接
     *
     * @return
     */
    public Connection getConnection() throws IOException, TimeoutException {
        return connectionFactory.newConnection();
    }

    /**
     * 关闭链接
     *
     * @param connection
     */
    public void closeConnection(Connection connection) {
        if (null != connection) {
            try {
                connection.close();
            } catch (Exception e) {
                LOGGER.error("closeConnection exception:[{}]", e);
            }
        }
    }

    /**
     * 关闭通道
     *
     * @param channel
     */
    public void closeChannel(Channel channel) {
        if (null != channel) {
            try {
                channel.close();
            } catch (Exception e) {
                LOGGER.error("closeChannel exception:[{}]", e);
            }
        }
    }

    /**
     * 创建queue
     *
     * @param name       queue名字
     * @param ifExchange true 创建新topic exchange（name）
     * @param channel    消息通道
     * @return
     */
    public void declareQueue(String name, boolean ifExchange, Channel channel) throws IOException {
        String exchange = DEFAULT_EXCHANGE;
        String routingKey = PREFIX_ROUTING_KEY + name;
        boolean durable = true;
        boolean autoDelete = false;
        if (ifExchange) {
            exchange = name;
            // 声明转发器 指定类型
            channel.exchangeDeclare(exchange, ExchangeType.topic.name(), durable, autoDelete, null);
        }
        //声明queue
        channel.queueDeclare(name, durable, false, autoDelete, null);
        //exchange & queue 绑定routingKey
        channel.queueBind(name, exchange, routingKey, null);
    }

    /**
     * 创建queue
     *
     * @param name       queue名字
     * @param ifExchange true 创建新topic exchange(name)，并且绑定的routingKey为 (#.name)
     * @throws IOException
     */
    public void declareQueue(String name, boolean ifExchange) {
        Connection connection = null;
        Channel channel = null;
        try {
            connection = getConnection();
            channel = connection.createChannel();
            declareQueue(name, ifExchange, channel);
        } catch (Exception e) {
            LOGGER.error("declareQueue exception:[{}]", e);
        } finally {
            closeChannel(channel);
            closeConnection(connection);
        }
    }

    /**
     * 创建queue 绑定在默认的exchange上 ，并且绑定的routingKey为 (#.name)
     *
     * @param name queue名字
     * @throws IOException
     */
    public void declareQueue(String name) {
        declareQueue(name, false);
    }


    /**
     * 发布消息
     *
     * @param message    发送的消息
     * @param exchange
     * @param routingKey
     * @param channel    消息通道，null则新建通道
     */
    public void send(Object message, String exchange, String routingKey, Channel channel) {
        if (null != channel) {
            try {
                _send(message, exchange, routingKey, channel);
            } catch (Exception e) {
                LOGGER.error("send exception:[{}]", e);
            }
        } else {
            Connection connection = null;
            try {
                connection = getConnection();
                channel = connection.createChannel();
                _send(message, exchange, routingKey, channel);
            } catch (Exception e) {
                LOGGER.error("send exception:[{}]", e);
            } finally {
                closeChannel(channel);
                closeConnection(connection);
            }
        }
    }

    //通过消息通道 发送消息
    private void _send(Object message, String exchange, String routingKey, Channel channel) throws IOException {

        channel.basicPublish(exchange, routingKey, MessageProperties.PERSISTENT_TEXT_PLAIN, JsonUtil.toString(message).getBytes());

    }

    /**
     * 发布消息
     *
     * @param message
     * @param queue
     * @param channel
     */
    public void send(Object message, String queue, Channel channel) {
        send(message, queue, queue, channel);
    }

    /**
     * 发布消息
     *
     * @param message
     * @param queue
     */
    public void send(Object message, String queue) {
        send(message, queue, null);
    }


    /**
     * 交换机类型
     */
    enum ExchangeType {
        fanout, direct, topic

    }


    /***********************************************************************************************************/
    /***********************************************************************************************************/
    /**使用rabbit client 主要是创建消费者**************************************************************************/
    /***********************************************************************************************************/
    /***********************************************************************************************************/

    /**
     * 创建消费者 * 每个消费者占用一个线程 消费一个q
     * 多个q，同一种消息处理器
     *
     * @param handler 处理消息
     * @param queue   消费的q
     * @throws Exception
     */
    public Connection createConsumer(final HandlerMsg handler, final String... queue) throws Exception {

        final Connection connection = getConnection();

        Channel channel = createConsumer(connection, handler, queue);

        return connection;
    }


    /**
     * 创建消费者 * 每个消费者占用一个线程 消费一个q
     * 多个q，同一种消息处理器
     *
     * @param connection rabbit连接
     * @param handler    处理消息
     * @param queue      消费的q
     * @throws Exception
     */
    private Channel createConsumer(final Connection connection, final HandlerMsg handler, final String... queue) throws Exception {

        final Channel channel = connection.createChannel();

        for (int i = 0; i < queue.length; i++) {
            final String q = queue[i];
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        createConsumer(q, handler, channel);
                    } catch (Exception e) {
                        //链接关闭，通道关闭 都会失败
                        LOGGER.error("createConsumer e:[{}]", e);
                    }
                }
            }).start();
        }

        LOGGER.info(Thread.currentThread().getName() + "==end==");
        return channel;
    }


    /**
     * 创建消费者
     *
     * @param queue   消费的q
     * @param handler 处理消息
     * @param channel 消息通道
     * @throws Exception
     */
    private void createConsumer(final String queue, final HandlerMsg handler, final Channel channel) throws Exception {
        LOGGER.info("[{}] createConsumer queue:[{}]", Thread.currentThread().getName(), queue);

        channel.basicQos(1);

        QueueingConsumer consumer = new QueueingConsumer(channel);
        boolean ack = false; //打开应答机制
        String consume = channel.basicConsume(queue, ack, consumer);
        for (; ; ) {
            if (StringUtils.isEmpty(consumer.getConsumerTag())) continue;

            //nextDelivery是一个阻塞方法（内部实现其实是阻塞队列的take方法）
            QueueingConsumer.Delivery delivery = consumer.nextDelivery();

            String message = new String(delivery.getBody());
            LOGGER.info("[{},{}] handler message:[{}]", Thread.currentThread().getName(), consumer.getConsumerTag(), message);
            /**处理消息逻辑*/
            handler.handlerMsg(message);

            //另外需要在每次处理完成一个消息后，手动发送一次应答。
            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
        }

    }

    /**
     * 消息处理
     */
    public interface HandlerMsg {
        void handlerMsg(String message);
    }


}
