package com.itheima.publisher;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @Author xiaohu
 * @Date 2024/12/6 9:24
 * @PackageName:com.itheima.publisher
 * @ClassName: SpringAmqpTest
 * @Description: 发消息到队列中 需要使用rabbitTemplate的api
 * @Version 1.0
 */
@SpringBootTest
@Slf4j
public class SpringAmqpTest {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
    * 发送消息到队列
    * */
    @Test
    public void testSimpleQueue() {
        // 1.队列名
        String queueName = "simple.queue";

        // 2.消息
        String message = "hello,spring amqp!";

        // 3.发送消息
        rabbitTemplate.convertAndSend(queueName,message);
    }

    @Test
    public void testWorkQueue() throws InterruptedException {
        // 1. 队列名称
        String queueName = "work.queue";

        // 2. 消息
        String message = "hello, workQueue Message!";

        for (int i = 0;i < 50; i++){
            // 发送消息，每20毫秒发送一次，相当于每秒发送50条消息
            rabbitTemplate.convertAndSend(queueName,message);
            Thread.sleep(1000);
        }
    }

    /**
     * 发送消息到fanout交换机
     * */
    @Test
    public void testFanoutExchange() {
        // 1. 交换机名
        String exchangeName = "hmall.fanout";

        // 2. 消息
        String message = "hello ,fanout exchange!";

        // 广播式发送到每个队列，没有routingKey绑定
        rabbitTemplate.convertAndSend(exchangeName,"",message);
    }

    /**
     * 发送消息到direct交换机
     * */
    @Test
    public void testDirectExchange() {
        // 1. 交换机名
        String exchangeName = "hmall.direct";

        // 2. 消息
        String message = "hello ,direct exchange!";

        // 发送到指定队列，有routingKey绑定
        rabbitTemplate.convertAndSend(exchangeName,"red",message);
    }

    /**
     * 如果所有队列都有这个routingKey,那么效果和fanout相同
     * 所有队列都会收到消息
    * */
    @Test
    public void testDirectExchange2() {
        // 1. 交换机名
        String exchangeName = "hmall.direct";

        // 2. 消息
        String message = "hello ,direct exchange!";

        // routingKey不同，消息发送到的队列也不同
        rabbitTemplate.convertAndSend(exchangeName,"blue",message);
    }

    /**
     * - #：匹配一个或多个词
     * - *：匹配不多不少恰好1个词
     * */
    @Test
    public void testSendTopicExchange() {
        // 1. 交换机名
        String exchangeName = "hmall.topic";

        // 2. 消息
        String message = "hello ,topic exchange!";

        // 发送到指定队列，有routingKey绑定(bindingKey可能是china.*)
        rabbitTemplate.convertAndSend(exchangeName,"china.news",message);
    }

    /**
     * 消息体类型是Object 可以是任意类型
     * 发送消息体不再是String类型，比如Map
     * JDK序列化存在下列问题：
         * - 数据体积过大
         * - 有安全漏洞
         * - 可读性差
     * */
    @Test
    public void testSendMap() {
        // 准备消息
        Map<String,Object> message = new HashMap<>();
        message.put("name","柳岩");
        message.put("age",21);

        // 发送消息
        rabbitTemplate.convertAndSend("object.queue",message);
    }

    @Test
    public void testConfirmCallback() {
        // 1. 交换机名
        String exchangeName = "hmall.direct";

        // 2. 消息
        String message = "hello ,direct exchange!";

        // 3. 创建CorrelationData
        CorrelationData cd = new CorrelationData(UUID.randomUUID().toString());

        cd.getFuture().addCallback(new ListenableFutureCallback<CorrelationData.Confirm>() {
            /**
             * 定义 springAMQP 在处理 MQ 返回的确认结果过程出错的处理方式
             * */
            @Override
            public void onFailure(Throwable ex) {
                // 2.1.Future发生异常时的处理逻辑，基本不会触发
                log.error("send message fail", ex);
            }

            @Override
            public void onSuccess(CorrelationData.Confirm result) {
                // 2.2.Future接收到回执的处理逻辑，参数中的result就是回执内容
                if (result.isAck()) {
                    log.debug("发送消息成功，收到ack！");
                } else {
                    log.error("发送消息失败，收到nack，reason ： {}", result.getReason());
                }
            }
        });

        // 发送消息
        rabbitTemplate.convertAndSend(exchangeName,"blue",message,cd);
    }

    /**
     * 验证消息持久化 和 非持久化 ;
     * 非持久化会造成 MQ阻塞 ，在内存放不下时，才会持久化到磁盘时MQ不会再接收消息
     * 在开启持久化机制之后，如果同时还开启了生产者确认，那么MQ会在消息持久化以后才发送ACK回执，进一步保证消息可靠性
     * 处于性能考虑，为了减少IO次数，发送到MQ的消息并不是逐条持久化到数据库的，而是【每隔一段时间批量持久化】。
     * 一般间隔在100毫秒左右，这就会导致ACK有一定的延迟，因此建议生产者全部采用异步方式
     * */
    @Test
    void testSendMessage() {
        // 1.自定义构建消息
        Message message = MessageBuilder
                .withBody("hello,SpringAMQP".getBytes(StandardCharsets.UTF_8))
                .setDeliveryMode(MessageDeliveryMode.PERSISTENT) // 持久化消息
//                .setDeliveryMode(MessageDeliveryMode.NON_PERSISTENT) // 非持久化消息
                .build();

        // 2.发送消息
        for (int i = 0; i < 1000000; i++) {
            rabbitTemplate.convertAndSend("simple.queue",message);
        }
    }

    @Test
    void testSendDelayMessage() {
        rabbitTemplate.convertAndSend("normal.direct","hi","hello",message -> {
            message.getMessageProperties().setExpiration("10000");
            return message;
        });
    }

    @Test
    void testSendDelayMessageByPlugin() {
        rabbitTemplate.convertAndSend("delay.direct","hi","hello",message -> {
            message.getMessageProperties().setDelay(10000);
            return message;
        });
    }
}
