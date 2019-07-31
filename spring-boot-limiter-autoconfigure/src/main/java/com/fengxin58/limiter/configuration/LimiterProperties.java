package com.fengxin58.limiter.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@ConfigurationProperties(prefix = "spring.limiter")
@Data
public final class LimiterProperties {
    /**
     * the redis key prefix
     */
    private String redisKeyPrefix = "#REDIS_LIMITER";

    /**
     * check action execution timeout(MILLISECONDS)
     */
    private int checkActionTimeout = 50000;

    /**
     * the flag to tell whether rate limiter configuration can change dynamically
     */
    private boolean enableDynamicalConf = false;

}
