package com.itheima.consumer.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.ImmediateRequeueMessageRecoverer;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.amqp.rabbit.retry.RepublishMessageRecoverer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author xiaohu
 * @version 1.0
 * @packageName com.itheima.consumer.config
 * @className ErrorMessageConfig
 * @date 2024/12/06 22:16
 * @description - RejectAndDontRequeueRecoverer：重试耗尽后，直接reject，丢弃消息。默认就是这种方式
              * -  ImmediateRequeueMessageRecoverer：重试耗尽后，返回nack，消息重新入队
              * -  RepublishMessageRecoverer：重试耗尽后，将失败消息投递到指定的交换机
 */
@Configuration
@ConditionalOnProperty(name = "spring.rabbitmq.listener.simple.retry.enabled", havingValue = "true") //在开启消费者失败重试机制的模块才加载的以下的bean
public class ErrorMessageConfig {

    @Bean
    public DirectExchange errorMessageExchange() {
        return new DirectExchange("error.direct");
    }
    @Bean
    public Queue errorQueue(){
        return new Queue("error.queue", true);
    }
    @Bean
    public Binding errorBinding(Queue errorQueue, DirectExchange errorMessageExchange){
        return BindingBuilder.bind(errorQueue).to(errorMessageExchange).with("error");
    }

    /**
     * 重试耗尽后，将失败消息投递到指定的交换机
     * */
    @Bean
    public MessageRecoverer republishMessageRecoverer(RabbitTemplate rabbitTemplate) {
        return new RepublishMessageRecoverer(rabbitTemplate,"error.direct","error");
    }

    /**
     * 重试耗尽后，返回nack，消息重新入队
    @Bean
    public MessageRecoverer ImmediateRequeueMessageRecoverer() {
        return new ImmediateRequeueMessageRecoverer();
    }*/
}
