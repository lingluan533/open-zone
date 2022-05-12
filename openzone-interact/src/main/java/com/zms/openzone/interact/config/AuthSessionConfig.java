package com.zms.openzone.interact.config;


import com.zms.openzone.common.constants.AuthServerConstant;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

/**
 * Description：设置Session作用域、自定义cookie序列化机制
 */
@Configuration
public class AuthSessionConfig {

    @Bean
    public CookieSerializer cookieSerializer() {
        DefaultCookieSerializer cookieSerializer = new DefaultCookieSerializer();
        // 明确的指定Cookie的作用域
        cookieSerializer.setDomainName("openzone.com");
        cookieSerializer.setCookieName(AuthServerConstant.SESSION);
        //将cookie中的GULISESSION对应的cookie值设置为与redis缓存中sessionId的值相同
        cookieSerializer.setUseBase64Encoding(false);
        //Sets the maxAge property of the Cookie. The default is to delete the cookie when the browser is closed.
        //Params:
        //cookieMaxAge – the maxAge property of the Cookie
        cookieSerializer.setCookieMaxAge(3600);
        return cookieSerializer;
    }

    /**
     * 自定义序列化机制
     * 这里方法名必须是：springSessionDefaultRedisSerializer
     */
    @Bean
    public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
        return new GenericJackson2JsonRedisSerializer();
    }
}
