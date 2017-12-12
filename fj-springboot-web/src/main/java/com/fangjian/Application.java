package com.fangjian;

import com.fangjian.xj.session.EnableSession;
import com.fangjian.xj.session.SessionGroup;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.session.SessionAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.connection.RedisConnectionFactory;

@SpringBootApplication(exclude = {SessionAutoConfiguration.class, DataSourceAutoConfiguration.class, RedisConnectionFactory.class,
        RedisAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class})
@ImportResource({"classpath*:spring/applicationContext-*.xml"})
@PropertySource({"classpath:db.properties", "classpath:common.properties", "classpath:redis.properties"})
@EnableSession(
        group = SessionGroup.USER,
        connectionFactory = "sessionJedisConnectionFactory"
)
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        System.out.println("The fj-springboot-web start success ...");
    }

}
