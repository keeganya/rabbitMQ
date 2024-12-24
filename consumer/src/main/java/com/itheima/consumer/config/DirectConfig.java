package com.itheima.consumer.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author xiaohu
 * @Date 2024/12/6 14:47
 * @PackageName:com.itheima.consumer.config
 * @ClassName: DirectConfig
 * @Description: 声明Direct类型交换机 和 队列
 *               一个队列通过routingKey绑定到交换机时，有多少个routingKey就需要创建多少个Bean 很麻烦
 * @Version 1.0
 */
//@Configuration
public class DirectConfig {

    /**
     * 声明Direct交换机
     * */
    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange("hmall.direct");
    }

    /**
    * 第一个队列
    * */
    @Bean
    public Queue directQueue1() {
        return new Queue("direct.queue1");
    }

    /**
    * 绑定队列和交换机 (routingKey 为 red)
    * */
    @Bean
    public Binding bindingQueue1WithRed() {
        return BindingBuilder.bind(directQueue1()).to(directExchange()).with("red");
    }

    /**
     * 绑定队列和交换机（routingKey 为 blue）
     * */
    @Bean
    public Binding bindingQueue1WithBlue() {
        return BindingBuilder.bind(directQueue1()).to(directExchange()).with("blue");
    }

    /**
     * 第二个队列
     * */
    @Bean
    public Queue directQueue2() {
        return QueueBuilder.durable("direct.queue2").build();
    }

    @Bean
    public Binding bindingQueue2WithRed() {
        return BindingBuilder.bind(directQueue2()).to(directExchange()).with("red");
    }

    @Bean
    public Binding bindingQueue2WithYellow() {
        return BindingBuilder.bind(directQueue2()).to(directExchange()).with("yellow");
    }
}
