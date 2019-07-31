package com.fengxin58.limiter.web.configuration;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fengxin58.limiter.configuration.LimiterProperties;
import com.fengxin58.limiter.web.CookiesPopulator;
import com.fengxin58.limiter.web.HeadersPopulator;

@Configuration
@EnableConfigurationProperties(LimiterProperties.class)
@ConditionalOnWebApplication
@AutoConfigureBefore(LimiterWebSupportAutoConfiguration.class)
public class WebPopulatorAutoConfiguration {

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
