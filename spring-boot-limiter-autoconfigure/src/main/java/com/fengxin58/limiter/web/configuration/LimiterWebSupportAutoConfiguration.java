package com.fengxin58.limiter.web.configuration;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fengxin58.limiter.RateChecker;
import com.fengxin58.limiter.configuration.LimiterProperties;
import com.fengxin58.limiter.web.CookiesPopulator;
import com.fengxin58.limiter.web.HeadersPopulator;
import com.fengxin58.limiter.web.RateCheckInterceptor;
import com.fengxin58.limiter.web.RateLimiterWebMvcConfigurer;
import com.fengxin58.limiter.web.RedisLimiterConfigProcessor;


@Configuration
@EnableConfigurationProperties(LimiterProperties.class)
@ConditionalOnWebApplication
public class LimiterWebSupportAutoConfiguration {

    @Autowired
    private LimiterProperties redisLimiterProperties;

    @Bean
    @ConditionalOnMissingBean(RateCheckInterceptor.class)
    public RateCheckInterceptor rateCheckInterceptor(RateChecker rateChecker) {
        RateCheckInterceptor rateCheckInterceptor;
        if (redisLimiterProperties.isEnableDynamicalConf()) {
            rateCheckInterceptor = new RateCheckInterceptor( rateChecker, redisLimiterConfigProcessor());
        } else {
            rateCheckInterceptor = new RateCheckInterceptor( rateChecker,null);
        }
        return rateCheckInterceptor;
    }

    @Bean
    @ConditionalOnMissingBean(RateLimiterWebMvcConfigurer.class)
    public RateLimiterWebMvcConfigurer rateLimiterWebMvcConfigurer(RateCheckInterceptor rateCheckInterceptor) {
        RateLimiterWebMvcConfigurer rateLimiterWebMvcConfigurer = new RateLimiterWebMvcConfigurer(rateCheckInterceptor);
        return rateLimiterWebMvcConfigurer;
    }
    
    @Bean
    @ConditionalOnMissingBean(RedisLimiterConfigProcessor.class)
    @ConditionalOnProperty(prefix = "spring.limiter", name = "enable-dynamical-conf", havingValue = "true")
    public RedisLimiterConfigProcessor redisLimiterConfigProcessor() {
        RedisLimiterConfigProcessor redisLimiterConfigProcessor = new RedisLimiterConfigProcessor();
        return redisLimiterConfigProcessor;
    }

    @Bean
    public CookiesPopulator cookiesPopulator() {
    	CookiesPopulator cookiesPopulator = new CookiesPopulator();
        return cookiesPopulator;
    }
    
    @Bean
    public HeadersPopulator headersPopulator() {
    	HeadersPopulator headersPopulator = new HeadersPopulator();
        return headersPopulator;
    }
    
}

