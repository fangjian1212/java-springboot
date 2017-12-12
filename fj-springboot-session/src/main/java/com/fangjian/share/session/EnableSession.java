/*
 */
package com.fangjian.share.session;

import com.fangjian.share.session.core.RedstarRedisSessionConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.session.data.redis.RedisFlushMode;

import java.lang.annotation.*;

/**
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = {ElementType.TYPE})
@Documented
@Configuration
@Import(RedstarRedisSessionConfiguration.class)
public @interface EnableSession {

    /**
     * 会话超时时间
     */
    int maxInactiveIntervalInSeconds() default 1800;

    /**
     * 会话保存模式
     */
    RedisFlushMode redisFlushMode() default RedisFlushMode.ON_SAVE;

    /**
     * 使用的redis连接工厂的bean名称,在spring中配置多个redis且没有配置session.properties时需要指定
     */
    String connectionFactory() default "";

    /**
     * 会话组(用户群)，不同的会话组之间隔离，会话不共享
     */
    SessionGroup group();

    /**
     * session id 在 cookie 中的名称
     */
    String cookieName() default Constants.DEFAULT_COOKIE_NAME;

    /**
     * cookieDomain 和 cookieDomainPattern 同时配置,时优先使用更加精确的cookieDomain
     */
    String cookieDomain() default "";

    String cookieDomainPattern() default Constants.DEFAULT_COOKIE_DOMAIN_PATTERN;

    /**
     * 是否允许创建session，默认：true
     */
    boolean allowCreateSession() default true;

    /**
     * 是否允许手动设置超时时间，默认：false
     */
    boolean allowManualTimeout() default false;

    /**
     * principal 在 session 中的键名
     */
    String principalSessionKey() default Constants.DEFAULT_PRINCIPAL_SESSION_KEY;

}
