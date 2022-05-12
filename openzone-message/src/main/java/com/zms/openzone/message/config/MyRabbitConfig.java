package com.zms.openzone.message.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * @author: zms
 * @create: 2022/2/23 20:10
 */
@Slf4j
@Configuration
public class MyRabbitConfig {

 //   @Autowired   这里如果自动注入的话会出现循环依赖的问题  可以自己定义一个RabbitTemplate 进行设置后，然后再把他放到容器中。
    private RabbitTemplate rabbitTemplate;


    @Primary
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory){
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        this.rabbitTemplate = rabbitTemplate;
        rabbitTemplate.setMessageConverter(messageConvert());
        // 设置回调
        initRabbitTemplate();
        return rabbitTemplate;
    }

    //使用JSON序列化机制，进行消息转换
      @Bean
        public MessageConverter messageConvert(){
            Jackson2JsonMessageConverter jackson2JsonMessageConverter = new Jackson2JsonMessageConverter();
            return jackson2JsonMessageConverter;
        }
        /*定制RabbitTemplate
           1.服务收到消息就回调  到到broke[exchange]
                1.设置spring.rabbitmq.publisher-confirms=true
                2.设置确认回调ConfirmCallback
           2.消息错误抵达队列时进行回调 达到queue
                 1.spring.rabbitmq.publisher-returns = true
                    spring.rabbitmq.template.mandatory=true
                 2.
        */

          //MyRabbitConfig对象创建完成后，执行这个方法
        public void initRabbitTemplate(){

            //设置确认回调
            rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
                /*
                * @param correlationData 当前消息的唯一关联数据（这个作为消息的唯一id）
                * @param ack 消息是否成功收到
                * @param cause 失败的原因
                * */

                public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                    System.out.println("confirm ... correlationData["+correlationData+"]===>ack["+ack+"]===>cause["+cause+"]");
                }
            });


            //设置消息抵达队列时的回调 [当消息没有投递给指定的队列时才调用此回调]
            rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {

                /*
                @param message 投递失败的消息详细信息
                @param replyCode 回复的状态码
                @param replyText 回复的文本内容
                @param exchange 当时这个消息发给那个交换机
                @param routeKey 当时这个消息使用的路由键
                *
                * */

                public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routeKey) {
                    System.out.println("Fail MessageEntity [" + message + "]" + "\treplyCode: " + replyCode + "\treplyText:" + replyText + "\texchange:" + exchange + "\trouterKey:" + routeKey);
                }
            });
        }
}
