package com.itheima.consumer.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author xiaohu
 * @Date 2024/12/6 14:47
 * @PackageName:com.itheima.consumer.config
 * @ClassName: DirectConfig
 * @Description: 声明正常交换机 和 队列 并绑定 死信交换机
 * @Version 1.0
 */
@Configuration
public class NormalConfig {

    /**
     * 声明普通交换机
     * */
    @Bean
    public DirectExchange normalExchange() {
        return new DirectExchange("normal.direct");
    }

    /**
    * 队列 绑定 死信交换机
    * */
    @Bean
    public Queue normalQueue() {
//        return new Queue("normal.queue");
        return QueueBuilder.durable("normal.queue").deadLetterExchange("dlx.direct").build();
    }

    /**
    * 绑定队列和交换机 (routingKey 为 hi)
    * */
    @Bean
    public Binding bindingQueueWithHi() {
        return BindingBuilder.bind(normalQueue()).to(normalExchange()).with("hi");
    }

}
