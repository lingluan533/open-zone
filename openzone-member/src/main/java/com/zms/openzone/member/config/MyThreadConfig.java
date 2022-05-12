package com.zms.openzone.member.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author: zms
 * @create: 2022/2/26 19:58
 */
@Configuration
public class MyThreadConfig {
    @Bean
    public ThreadPoolExecutor threadPoolExecutor(ThreadPoolConfigProperties threadPoolConfigProperties) {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(20,//threadPoolConfigProperties.getCoreSize(),
               200, //threadPoolConfigProperties.getMaxSize(),
               10,// threadPoolConfigProperties.getKeepAliveTime(),
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(100000),
                new ThreadPoolExecutor.AbortPolicy()
        );
        return executor;
    }


}
