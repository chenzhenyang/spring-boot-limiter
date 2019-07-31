package com.fengxin58.limiter.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fengxin58.limiter.IRateLimiterFactory;
import com.fengxin58.limiter.RateLimiterChecker;
import com.fengxin58.limiter.event.DefaultRateCheckFailureListener;
import com.fengxin58.limiter.event.DefaultRateExceedingListener;
import com.fengxin58.limiter.event.RateCheckFailureListener;
import com.fengxin58.limiter.event.RateExceedingListener;

@Configuration
@EnableConfigurationProperties(LimiterProperties.class)
public class LimiterAutoConfiguration {

    @Autowired
    private LimiterProperties limiterProperties;
    
    @Bean
    @ConditionalOnMissingBean(RateLimiterChecker.class)
    public RateLimiterChecker rateLimiterChecker(IRateLimiterFactory rateLimiterFactory) {
    	RateLimiterChecker rateChecker = new RateLimiterChecker(rateLimiterFactory,limiterProperties.getCheckActionTimeout());
        return rateChecker;
    }

    @Bean
    @ConditionalOnMissingBean(RateCheckFailureListener.class)
    public RateCheckFailureListener rateCheckFailureListener() {
        RateCheckFailureListener rateCheckFailureListener = new DefaultRateCheckFailureListener();
        return rateCheckFailureListener;
    }

    @Bean
    @ConditionalOnMissingBean(RateExceedingListener.class)
    public RateExceedingListener rateExceedingListener() {
        RateExceedingListener rateExceedingListener = new DefaultRateExceedingListener();
        return rateExceedingListener;
    }

}
