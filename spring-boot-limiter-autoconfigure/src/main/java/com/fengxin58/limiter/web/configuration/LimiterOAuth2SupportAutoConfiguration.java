package com.fengxin58.limiter.web.configuration;


import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import com.fengxin58.limiter.configuration.LimiterProperties;
import com.fengxin58.limiter.web.AuthenticationPopulator;


@Configuration
@EnableConfigurationProperties(LimiterProperties.class)
@ConditionalOnWebApplication
@ConditionalOnClass({Authentication.class,OAuth2Authentication.class})
public class LimiterOAuth2SupportAutoConfiguration {

    @Bean
    public AuthenticationPopulator authenticationPopulator() {
    	AuthenticationPopulator authenticationPopulator = new AuthenticationPopulator();
        return authenticationPopulator;
    }
    
}

