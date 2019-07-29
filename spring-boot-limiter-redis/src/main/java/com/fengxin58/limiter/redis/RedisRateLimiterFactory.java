package com.fengxin58.limiter.redis;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.connection.RedisConnectionFactory;

import com.fengxin58.limiter.IRateLimiter;
import com.fengxin58.limiter.IRateLimiterFactory;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class RedisRateLimiterFactory implements IRateLimiterFactory{

	private final RedisConnectionFactory redisConnectionFactory;

    private Cache<TimeUnit, IRateLimiter> redisRateLimiterCache =
            Caffeine.newBuilder().maximumSize(10).build();


    public IRateLimiter get(TimeUnit timeUnit) {
        IRateLimiter redisRateLimiter = redisRateLimiterCache.getIfPresent(timeUnit);
        if(redisRateLimiter == null) {
            redisRateLimiter = new RedisRateLimiter(redisConnectionFactory, timeUnit);
            redisRateLimiterCache.put(timeUnit, redisRateLimiter);
        }
        return redisRateLimiter;
    }
}
