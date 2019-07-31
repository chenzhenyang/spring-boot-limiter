package com.fengxin58.limiter.web.configuration;

import java.util.Collection;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fengxin58.limiter.RateLimiterChecker;
import com.fengxin58.limiter.configuration.LimiterProperties;
import com.fengxin58.limiter.web.IStandardEvaluationContextPopulator;
import com.fengxin58.limiter.web.RateLimiterCheckInterceptor;
import com.fengxin58.limiter.web.RateLimiterWebMvcConfigurer;
import com.fengxin58.limiter.web.RedisLimiterConfigProcessor;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableConfigurationProperties(LimiterProperties.class)
@ConditionalOnWebApplication
public class LimiterWebSupportAutoConfiguration implements ApplicationContextAware {

	@Autowired
	private LimiterProperties limiterProperties;

	@Bean
	@ConditionalOnMissingBean(RateLimiterCheckInterceptor.class)
	public RateLimiterCheckInterceptor rateCheckInterceptor(RateLimiterChecker rateChecker) {
		RateLimiterCheckInterceptor rateCheckInterceptor;
		if (limiterProperties.isEnableDynamicalConf()) {
			rateCheckInterceptor = new RateLimiterCheckInterceptor(rateChecker, redisLimiterConfigProcessor());
		} else {
			rateCheckInterceptor = new RateLimiterCheckInterceptor(rateChecker, null);
		}

		Map<String, IStandardEvaluationContextPopulator> beansOfType = applicationContext
				.getBeansOfType(IStandardEvaluationContextPopulator.class);
		Collection<IStandardEvaluationContextPopulator> populators = beansOfType.values();
		
		if(log.isDebugEnabled()) {
			log.debug("Spring Boot Limiter is started on webApplication");
			populators.forEach(populator->{
				log.debug("Found " + populator.getClass() + " instance for RateLimiterCheckInterceptor");
			});	
		}
		
		rateCheckInterceptor.setPopulators(populators);
		
		return rateCheckInterceptor;
	}

	@Bean
	@ConditionalOnMissingBean(RateLimiterWebMvcConfigurer.class)
	public RateLimiterWebMvcConfigurer rateLimiterWebMvcConfigurer(RateLimiterCheckInterceptor rateCheckInterceptor) {
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

	private ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

}
