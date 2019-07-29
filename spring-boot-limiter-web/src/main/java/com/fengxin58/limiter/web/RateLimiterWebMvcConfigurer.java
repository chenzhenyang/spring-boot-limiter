package com.fengxin58.limiter.web;

import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

public final class RateLimiterWebMvcConfigurer implements WebMvcConfigurer {

    private final RateCheckInterceptor rateCheckInterceptor;
    
    public RateLimiterWebMvcConfigurer(RateCheckInterceptor rateCheckInterceptor) {
    	this.rateCheckInterceptor = rateCheckInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(rateCheckInterceptor).addPathPatterns("/**").order(Ordered.HIGHEST_PRECEDENCE);
    }
}
