package com.itheima.consumer.mq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.Map;

/**
 * @Author xiaohu
 * @Date 2024/12/6 9:31
 * @PackageName:com.itheima.consumer.mq
 * @ClassName: SpringRabbitListener
 * @Description: 通过 @RabbitListener 一直监听 simple.queue 队列
 *               监听到的消息会传入到方法参数中去
 * @Version 1.0
 */
@Slf4j
@Component
public class SpringRabbitListener {

    @RabbitListener(queues = "simple.queue")
    public void listenSimpleQueue(String message) {
        log.info("监听到simple.queue队列的消息：【{}】", message);
    }

    @RabbitListener(queues = "simple.queue")
    public void listenSimpleQueue1(Message message) {
        log.info("监听到simple.queue队列的消息：【{}】", new String(message.getBody()));
        log.info("监听到simple.queue队列的消息 ID：【{}】", message.getMessageProperties().getMessageId());
    }

    /**
     * 多个消费者监听同一个队列，可以加快消息处理速度
     * */
    @RabbitListener(queues = "work.queue")
    public void listenWorkQueue1(String message) throws InterruptedException {
        System.out.println("消费者1接收到消息：【" + message + "】" + LocalTime.now());
        Thread.sleep(20);
    }
    @RabbitListener(queues = "work.queue")
    public void listenWorkQueue2(String message) throws InterruptedException {
        System.out.println("消费者2接收到消息：【" + message + "】" + LocalTime.now());
        Thread.sleep(200);
    }

    /**
     * fanout交换机
     * 多个消费者监听不同的队列（fanout.queue1,fanout.queue2），队列消息由同一个交换机(hmall.fanout)发送
     * */

    @RabbitListener(queues = "fanout.queue1")
    public void listenFanoutQueue1(String message) {
        System.out.println("消费者1接收到消息：【" + message + "】" + LocalTime.now());
    }

    @RabbitListener(queues = "fanout.queue2")
    public void listenFanoutQueue2(String message) {
        System.out.println("消费者2接收到消息：【" + message + "】" + LocalTime.now());
    }

    /**
     * direct交换机
     * */
//    @RabbitListener(queues = "direct.queue1")
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "direct.queue1"),
            exchange = @Exchange(name = "hmall.direct",type = ExchangeTypes.DIRECT),
            key = {"red","blue"}
    ))
    public void listenDirectQueue1(String message) {
        System.out.println("消费者1接收到消息：【" + message + "】" + LocalTime.now());
    }

//    @RabbitListener(queues = "direct.queue2")
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "direct.queue2", durable = "true",
                    /** 设置队列 Queue 为 lazy 模式*/
                    arguments = @Argument(name = "x-queue-mode",value = "lazy")),
            exchange = @Exchange(name = "hmall.direct",type = ExchangeTypes.DIRECT),
            key = {"red","yellow"}
    ))
    public void listenDirectQueue2(String message) {
        System.out.println("消费者2接收到消息：【" + message + "】" + LocalTime.now());
    }

    /**
     * topic交换机
     * topic.queue1 routingKey : china.*
     * */
//    @RabbitListener(queues = "topic.queue1")
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "topic.queue1"),
            exchange = @Exchange(name = "hamll.topic",type = ExchangeTypes.TOPIC),
            key = "china.*"
    ))
    public void listenTopicQueue1(String message) {
        System.out.println("消费者1接收到消息：【" + message + "】" + LocalTime.now());
    }

    /**
     * topic.queue2 routingKey : #.news
     * */
//    @RabbitListener(queues = "topic.queue2")
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "topic.queue2"),
            exchange = @Exchange(name = "hmall.topic",type = ExchangeTypes.TOPIC),
            key = "#.news"
    ))
    public void listenTopicQueue2(String message) {
        System.out.println("消费者2接收到消息：【" + message + "】" + LocalTime.now());
    }


    @RabbitListener(queues = "object.queue")
    public void listenObjectQueue(Map<String, Object> message) {
        System.out.println("消费者接收到object.queue消息：【" + message + "】");
    }



    /**
     * 声明死信交换机和队列
     * 死信定义：
         * - 消费者使用basic.reject或 basic.nack声明消费失败，并且消息的requeue参数设置为false
         * - 消息是一个过期消息，超时无人消费
         * - 要投递的队列消息满了，最早的消息可能成为死信
    * */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "dlx.queue"),
            exchange = @Exchange(name = "dlx.direct",type = ExchangeTypes.DIRECT),
            key = {"hi"}
    ))
    public void listenDlxQueue(String message) {
        System.out.println("消费者接收到dlx.queue的消息：【" + message + "】" + LocalTime.now());
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "delay.queue"),
            exchange = @Exchange(name = "delay.direct",delayed = "true"),
            key = {"hi"}
    ))
    public void listenDelayQueue(String message) {
        System.out.println("消费者接收到delay.queue的消息：【" + message + "】" + LocalTime.now());
    }

}
