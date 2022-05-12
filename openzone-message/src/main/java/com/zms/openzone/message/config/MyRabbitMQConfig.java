package com.zms.openzone.message.config;


import com.zms.openzone.common.constants.RabbitInfo;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 消息队列相关配置
 *
 * @author: zms
 * @create: 2022/3/2 16:49
 */
@Configuration
public class MyRabbitMQConfig {
    //定义交换机
    @Bean
    public Exchange interactEventExchange() {
        return new TopicExchange(RabbitInfo.Interact.exchange, true, false);
    }

    //定义三个消息队列
    @Bean
    public Queue commentQueue() {
        //(String name, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments) {
        return new Queue(RabbitInfo.Interact.commentQueue, true, false, false);
    }


    @Bean
    public Queue likeQueue() {
        //(String name, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments) {
        return new Queue(RabbitInfo.Interact.likeQueue, true, false, false);
    }

    @Bean
    public Queue followQueue() {
        //(String name, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments) {
        return new Queue(RabbitInfo.Interact.followQueue, true, false, false);
    }

    //定义交换机与队列的绑定关系
    @Bean
    public Binding commentQueueBinding() {
/*              (String destination,
               Binding.DestinationType destinationType,
               String exchange,
               String routingKey,
               java.util.Map<String, Object> arguments)*/
        return new Binding(RabbitInfo.Interact.commentQueue, Binding.DestinationType.QUEUE,
                RabbitInfo.Interact.exchange, RabbitInfo.Interact.commentRoutingKey, null);
    }

    @Bean
    public Binding likeQueueBinding() {
        return new Binding(RabbitInfo.Interact.likeQueue, Binding.DestinationType.QUEUE,
                RabbitInfo.Interact.exchange, RabbitInfo.Interact.likeRoutingKey, null);
    }

    @Bean
    public Binding followQueueBinding() {
        return new Binding(RabbitInfo.Interact.followQueue, Binding.DestinationType.QUEUE,
                RabbitInfo.Interact.exchange, RabbitInfo.Interact.followRoutingKey, null);
    }

}
