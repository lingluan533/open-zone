package com.zms.openzone.interact.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

/**
 * @author: zms
 * @create: 2022/2/26 19:59
 */

@Component
@Data

public class ThreadPoolConfigProperties {
    /*
    * #openzone:
#  thread:
#    core-size: 20
#    max-size: 200
#    keep-alive-time: 10
    * */
    @Value("${openzone.thread.coreSize}")
    private Integer coreSize;
    @Value("${openzone.thread.maxSize}")
    private Integer maxSize;
    @Value("${openzone.thread.keepAliveTime}")
    private Integer keepAliveTime;
}
