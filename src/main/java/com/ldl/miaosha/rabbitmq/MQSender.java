package com.ldl.miaosha.rabbitmq;

import com.ldl.miaosha.redis.RedisService;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MQSender {

    @Autowired
    AmqpTemplate amqpTemplate;

    public void SendMiaoshaMessage(MiaoshaMessage miaoshaMessage) {
        String message = RedisService.BeanToString(miaoshaMessage);
        amqpTemplate.convertAndSend(MQConfig.MIAOSHA_QUEUE, message);
    }
}
