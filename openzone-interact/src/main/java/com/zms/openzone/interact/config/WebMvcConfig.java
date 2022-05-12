package com.zms.openzone.interact.config;

import com.zms.openzone.interact.interceptor.LoginUserInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author: zms
 * @create: 2022/3/1 20:37
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Autowired
    private LoginUserInterceptor loginUserInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //针对互动服务下需要登录才能访问的接口进行设置登录拦截器
        registry.addInterceptor(loginUserInterceptor).addPathPatterns("/like/**", "/discuss/add/**", "/comment/**");
    }
}
