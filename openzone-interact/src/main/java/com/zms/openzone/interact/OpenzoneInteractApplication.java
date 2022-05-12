package com.zms.openzone.interact;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.core.env.Environment;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
/*
 *
 * /**
 * 整合RabbitMQ
 * 1.引入AMQP场景启动器
 * <dependency>
 *             <groupId>org.springframework.boot</groupId>
 *             <artifactId>spring-boot-starter-amqp</artifactId>
 *         </dependency>
 * 2.给容器中自动配置了
 * 		RabbitTemplate 、 AmqpAdmin 、CachingConnectionFactory 、 RabbitMessagingTemplate
 * 		所有的属性都是以spring.rabbitmq开头的
 * 		@ConfigurationProperties(prefix="spring.rabbitmq")
 * 		public class RabbitProperties
 * 3.在spring的配置文件中配置spring.rabbitmq开头的相关信息

 * 4.在微服务的主启动类上添加 @EnableRabbit 开启功能
 * 5. 监听消息：@RabbitListener: 监听队列 [这个队列必须存在]必须已经开启Rabbit  @EnableRabbit
 * @RabbitListener: 可以用在类 + 方法上
 * @RabbitHandler:标在方法上
 * 	第一次启动报了RabbitMQ相关的error别慌 这是RabbitMQ正在创建队列 、交换机、绑定信息 还没刷新导致的
 **/

@EnableRabbit
@EnableRedisHttpSession
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
public class OpenzoneInteractApplication {
    @Resource
    private Environment env;
    public static void main(String[] args) {
        SpringApplication.run(OpenzoneInteractApplication.class, args);
    }
    @Resource
    private void configureThymeleafStaticVars(ThymeleafViewResolver viewResolver) {
        if(viewResolver != null) {
            /*
authUrl: http://auth.lingluan.vip
messageUrl: http://message.lingluan.vip
interactUrl: http://interact.lingluan.vip
memberUrl: http://member.lingluan.vip
searchUrl: http://search.lingluan.vip
Url: http://lingluan.vip
            * */
            Map<String, Object> vars = new HashMap<String, Object>();
            vars.put("authUrl", env.getProperty("authUrl"));
            vars.put("messageUrl", env.getProperty("messageUrl"));
            vars.put("interactUrl", env.getProperty("interactUrl"));
            vars.put("memberUrl", env.getProperty("memberUrl"));
            vars.put("searchUrl", env.getProperty("searchUrl"));
            vars.put("Url", env.getProperty("Url"));

            System.out.println("域名："+ env.getProperty("Url"));
            viewResolver.setStaticVariables(vars);
        }
    }
}
