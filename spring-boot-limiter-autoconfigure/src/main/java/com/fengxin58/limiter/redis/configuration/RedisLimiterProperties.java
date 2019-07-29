package com.fengxin58.limiter.redis.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.limiter.redis")
@Data
public final class RedisLimiterProperties {
    /**
     * channel for pub/sub limiter configuration change event
     */
    private String channel = "#RLConfigChannel";
}
