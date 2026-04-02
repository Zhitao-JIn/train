package com.zhitao.train.business.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {

    @Bean(destroyMethod = "shutdown")
    public RedissonClient redissonClient() {
        Config config = new Config();

        // 单节点模式
        config.useSingleServer()
                .setAddress("redis://tt-bp1a92ngkq7qwwl3wpd.tairskv.rds.aliyuncs.com:6379")
                .setPassword("*CYYKx05K^GiK*z^)KO") // 如果有密码
                .setConnectionMinimumIdleSize(1)
                .setConnectionPoolSize(10)
                .setTimeout(10000); // 毫秒

        return Redisson.create(config);
    }
}