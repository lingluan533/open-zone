package com.zms.openzone.getaway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class OpenzoneGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(OpenzoneGatewayApplication.class, args);
    }

}
