package com.zms.openzone.search;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.core.env.Environment;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class OpenzoneSearchApplication {
    @Resource
    private Environment env;
    public static void main(String[] args) {
        SpringApplication.run(OpenzoneSearchApplication.class, args);
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
