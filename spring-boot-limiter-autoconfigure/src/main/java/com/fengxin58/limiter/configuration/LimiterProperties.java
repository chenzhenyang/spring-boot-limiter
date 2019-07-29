package com.fengxin58.limiter.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

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
    private int checkActionTimeout = 100;

    /**
     * the flag to tell whether rate limiter configuration can change dynamically
     */
    private boolean enableDynamicalConf = false;

}
