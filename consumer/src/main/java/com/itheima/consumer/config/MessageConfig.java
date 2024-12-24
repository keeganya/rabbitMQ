package com.itheima.consumer.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * @Author xiaohu
 * @Date 2024/12/6 15:33
 * @PackageName:com.itheima.consumer.config
 * @ClassName: MessageConfig
 * @Description: TODO
 * @Version 1.0
 */
@Configuration
public class MessageConfig {
    @Bean
    public Queue objectQueue() {
        return QueueBuilder.durable("object.queue").build();
    }
}
