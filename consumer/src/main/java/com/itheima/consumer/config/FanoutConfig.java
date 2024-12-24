package com.itheima.consumer.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author xiaohu
 * @Date 2024/12/6 14:31
 * @PackageName:com.itheima.consumer.config
 * @ClassName: FanoutConfig
 * @Description: 声明fanout类型交换机 和 队列
 * @Version 1.0
 */
//@Configuration
public class FanoutConfig {

    /**
     * fanout类型交换机
     * */
    @Bean
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange("hmall.fanout");
//        return ExchangeBuilder.fanoutExchange("hmall.fanout").build();
    }

    /**
     * 第一个队列
     * */
    @Bean
    public Queue fanoutQueue1() {
        return new Queue("fanout.queue1");
//        return QueueBuilder.durable("hmall.queue1").build();
    }

    /**
     * 绑定队列和交换机
     * */
    @Bean
    public Binding bindingQueue1(Queue fanoutQueue1, FanoutExchange fanoutExchange) {
        return BindingBuilder.bind(fanoutQueue1).to(fanoutExchange);
    }

    /**
     * 第二个队列
     * */
    @Bean
    public Queue fanoutQueue2() {
        return new Queue("fanout.queue2");
    }

    /**
     * 绑定队列和交换机
     * */
    @Bean
    public Binding bindingQueue2(Queue fanoutQueue2, FanoutExchange fanoutExchange) {
        return BindingBuilder.bind(fanoutQueue2).to(fanoutExchange);
    }


}
