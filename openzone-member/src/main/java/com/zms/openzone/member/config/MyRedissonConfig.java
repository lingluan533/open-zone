package com.zms.openzone.member.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author: zms
 * @create: 2021/12/31 16:28
 */
@Configuration
public class MyRedissonConfig {
    @Value("${spring.redis.host}")
    private String redisHost;
    @Value("${spring.redis.port}")
    private String redisPort;
    @Value("${spring.redis.password}")
    private String redisPassword;
    @Bean(destroyMethod = "shutdown")

    public RedissonClient redisson() {
        //1.创建配置
        Config config = new Config();
        config.useSingleServer().setAddress("redis://"+redisHost+":"+redisPort);
        config.useSingleServer().setPassword(redisPassword);
        //2.根据配置文件生成client
        return Redisson.create(config);
    }
}
