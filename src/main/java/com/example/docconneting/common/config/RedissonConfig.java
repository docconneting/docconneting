package com.example.docconneting.common.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "redisson.enabled", havingValue = "true")
public class RedissonConfig {

    @Value("${redisson.address}")
    private String redisAddress;

    @Value("${redisson.connection-pool-size}")
    private int connectionPoolSize;

    @Value("${redisson.idle-connection-min}")
    private int idleConnectionMin;

    @Value("${redisson.timeout}")
    private int timeout;

    @Bean(destroyMethod = "shutdown")
    public RedissonClient redissonClient() {
        if (!redisAddress.startsWith("redis://") && !redisAddress.startsWith("rediss://")) {
            throw new IllegalArgumentException("Invalid Redis address. Must start with redis:// or rediss://");
        }

        Config config = new Config();
        config.useSingleServer()
                .setAddress(redisAddress)
                .setConnectionPoolSize(connectionPoolSize)
                .setConnectionMinimumIdleSize(idleConnectionMin)
                .setTimeout(timeout);

        return Redisson.create(config);
    }
}
