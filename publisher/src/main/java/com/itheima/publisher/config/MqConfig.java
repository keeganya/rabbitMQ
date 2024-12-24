package com.itheima.publisher.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * @Author xiaohu
 * @Date 2024/12/6 17:33
 * @PackageName:com.itheima.publisher.config
 * @ClassName: MqConfig
 * @Description: 生产者发送到MQ成功，但是路由失败，会触发下面的 ReturnsCallback （记录路由失败的相关信息）
 * @Version 1.0
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class MqConfig {
    private final RabbitTemplate rabbitTemplate;

    @PostConstruct
    public void init() {
        rabbitTemplate.setReturnsCallback(new RabbitTemplate.ReturnsCallback() {
            @Override
            public void returnedMessage(ReturnedMessage returnedMessage) {
                log.error("触发return callback,");
                log.debug("exchange: {}", returnedMessage.getExchange());
                log.debug("routingKey: {}", returnedMessage.getRoutingKey());
                log.debug("message: {}", returnedMessage.getMessage());
                log.debug("replyCode: {}", returnedMessage.getReplyCode());
                log.debug("replyText: {}", returnedMessage.getReplyText());
            }
        });
    }
}
