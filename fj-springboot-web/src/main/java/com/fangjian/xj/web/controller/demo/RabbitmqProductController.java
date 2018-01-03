package com.fangjian.xj.web.controller.demo;

import com.fangjian.framework.mq.rabbitmq.utils.ClientRabbitmqUtil;
import com.fangjian.framework.mq.rabbitmq.utils.ProducerUtil;
import com.fangjian.framework.mq.rabbitmq.utils.RabbitmqUtil;
import com.fangjian.framework.rest.result.vo.RestResultVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;


/**
 * @description:
 * @author: fangjian<jian.fang@chinaredstar.com>
 * @date: Create in 11:08 2017/11/30
 * @modified by:
 */
@RestController
@RequestMapping("/rabbit")
public class RabbitmqProductController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitmqProductController.class);


    /**
     * demo
     *
     * @return
     */
    @RequestMapping(value = "/demo", method = {RequestMethod.GET})
    public RestResultVO<Object> demo(String message) throws Exception {

        String exchange = "topic.fangjian";
        ClientRabbitmqUtil clientRabbitmqUtil = ClientRabbitmqUtil.getRabbitmqUtil(exchange);

        String q = "jc_sms_05";


//        clientRabbitmqUtil.declareQueue(q, true);

//        clientRabbitmqUtil.send(new Fangjian("ffjjxx", 90, new Date()), q);
        ProducerUtil.send(new Fangjian("fjxj1212", 12, new Date()), q);


//        clientRabbitmqUtil.createConsumer(new ClientRabbitmqUtil.HandlerMsg() {
//            @Override
//            public void handlerMsg(String message) {
//                System.out.println("handlerMsg" + message);
//            }
//        }, q, q);

        return new RestResultVO("ok");
    }

    /**
     * rabbit
     *
     * @return
     */
    @RequestMapping(value = "/send", method = {RequestMethod.GET})
    public RestResultVO<Object> rabbit(String message) throws Exception {

        String exchange = "topic.fangjian";
        RabbitmqUtil rabbitmqUtil = RabbitmqUtil.getRabbitmqUtil(exchange);

//        ProducerUtil.send(message, EXCHANGE, ROUTING_KEY);

        String q = "fj_order_01";

//        rabbitmqUtil.deleteQueue(q);

        String queue = rabbitmqUtil.declareQueue(q, true);
        ProducerUtil.send(new Fangjian("fjxj1212", 12, new Date()), q);


        return new RestResultVO("ok");
    }

}
