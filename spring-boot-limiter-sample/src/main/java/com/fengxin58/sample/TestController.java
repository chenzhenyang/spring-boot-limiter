package com.fengxin58.sample;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fengxin58.limiter.DynamicRateLimiter;
import com.fengxin58.limiter.RateLimiter;

import java.util.concurrent.TimeUnit;


@RestController
@RequestMapping("/demo")
public class TestController {
	
	@GetMapping("/oauth2Test")
    //基于用户限流，独立用户每分钟最多2次访问，用户id在header中，key为userid
    //RateLimiter标签为静态配置，此类配置不可动态修改
    @RateLimiter(base = "#JwtClaims['user_name']", permits = 2, timeUnit = TimeUnit.MINUTES) 
    public String oauth2Test() {
        return "test!";
    }
    
    @GetMapping("/test")
    //基于用户限流，独立用户每分钟最多2次访问，用户id在header中，key为userid
    //RateLimiter标签为静态配置，此类配置不可动态修改
    @RateLimiter(base = "#Headers['userid']", permits = 2, timeUnit = TimeUnit.MINUTES) 
    public String test() {
        return "test!";
    }

    @GetMapping("/dynamictest")
    //基于来源ip限流，独立ip每分钟最多访问5次访问，来源ip位于header中，key为X-Real-IP
    //DynamicRateLimiter标签代表动态配置，此类配置可在运行时动态修改
    @DynamicRateLimiter(base = "#Headers['x-real-ip']", permits = 5, timeUnit = TimeUnit.MINUTES)
    public String dynamicTest() {
        return "dynamictest!";
    }
}
