package com.fengxin58.limiter.redis.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import com.fengxin58.limiter.IRateLimiterFactory;
import com.fengxin58.limiter.configuration.LimiterAutoConfiguration;
import com.fengxin58.limiter.configuration.LimiterProperties;
import com.fengxin58.limiter.redis.RedisRateLimiterFactory;

@Configuration
@EnableConfigurationProperties(RedisLimiterProperties.class)
@AutoConfigureBefore(LimiterAutoConfiguration.class)
public class RedisLimiterAutoConfiguration {

	@Autowired
	private RedisLimiterProperties redisLimiterProperties;
	
	@Autowired
	private LimiterProperties limiterProperties;

	@Bean
	@ConditionalOnMissingBean(RedisRateLimiterFactory.class)
	public IRateLimiterFactory rateLimiterFactory(RedisConnectionFactory redisConnectionFactory) {
		RedisRateLimiterFactory redisRateLimiterFactory = new RedisRateLimiterFactory(redisConnectionFactory);
		return redisRateLimiterFactory;
	}
	

}
