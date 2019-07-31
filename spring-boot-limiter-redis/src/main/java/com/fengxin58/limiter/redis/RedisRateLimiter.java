package com.fengxin58.limiter.redis;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fengxin58.limiter.IRateLimiter;

import lombok.extern.slf4j.Slf4j;

/**
 * Redis based Rate limiter
 *
 * @author chenzhenyang czy_1@qq.com>
 */
@Slf4j
public final class RedisRateLimiter implements IRateLimiter {

	@SuppressWarnings("rawtypes")
	private final RedisTemplate redisTemplate;

	private final RedisUtils redisUtils;

	private final TimeUnitCalculator timeUnitCalculator;

	private final TimeUnit timeUnit;

	private final DefaultRedisScript<Long> LUA_SECOND_SCRIPT = new DefaultRedisScript<>();

	private final DefaultRedisScript<Long> LUA_PERIOD_SCRIPT = new DefaultRedisScript<>();

	public RedisRateLimiter(RedisConnectionFactory redisConnectionFactory, TimeUnit timeUnit) {
		this.timeUnit = timeUnit;
		this.timeUnitCalculator = new TimeUnitCalculator(timeUnit);
		redisTemplate = new StringRedisTemplate(redisConnectionFactory);

		redisUtils = new RedisUtils(redisTemplate);

		LUA_SECOND_SCRIPT.setLocation(new ClassPathResource("lua/second.lua"));
		LUA_SECOND_SCRIPT.setResultType(Long.class);

		LUA_PERIOD_SCRIPT.setLocation(new ClassPathResource("lua/period.lua"));
		LUA_PERIOD_SCRIPT.setResultType(Long.class);
	}

	public boolean acquire(String key, int permitsPerUnit) {
		boolean rtv = false;
		if (timeUnit == TimeUnit.SECONDS) {
			rtv = doSecond(key, permitsPerUnit);
		} else if (timeUnit == TimeUnit.MINUTES || timeUnit == TimeUnit.HOURS || timeUnit == TimeUnit.DAYS) {
			try {
				rtv = doPeriod(key, permitsPerUnit);
			} catch (InterruptedException | ExecutionException e) {
				log.error(e.getMessage(), e);
			}
		}
		return rtv;
	}

	private boolean doSecond(String key, int permitsPerUnit) {
		String keyName = getKeyNameForSecond(key);
		List<Object> keys = new ArrayList<>(1);
		keys.add(keyName);
		@SuppressWarnings("unchecked")
		Object val = redisTemplate.execute(LUA_SECOND_SCRIPT, new StringRedisSerializer(), new StringRedisSerializer(),
				keys, String.valueOf(timeUnitCalculator.getExpire()), String.valueOf(permitsPerUnit));
		boolean rtv = (Long.parseLong(val.toString()) > 0);
		return rtv;
	}

	@SuppressWarnings("unchecked")
	private boolean doPeriod(String keyPrefix, int permitsPerUnit) throws InterruptedException, ExecutionException {
		String[] keyNames = getKeyNames(keyPrefix);
		Long currentTimeInMicroSecond = redisUtils.getServerTimeInMicros();
		String previousSectionBeginScore = String
				.valueOf((currentTimeInMicroSecond - timeUnitCalculator.getPeriodMicrosecond()));
		String expires = String.valueOf(timeUnitCalculator.getExpire());
		String currentTimeInMicroSecondStr = String.valueOf(currentTimeInMicroSecond);
		List<String> keys = new ArrayList<String>(2);
		keys.add(keyNames[0]);
		keys.add(keyNames[1]);
		try {
			Object val = redisTemplate.execute(LUA_PERIOD_SCRIPT, new StringRedisSerializer(),
					new StringRedisSerializer(), keys, currentTimeInMicroSecondStr, currentTimeInMicroSecondStr,
					previousSectionBeginScore, expires, String.valueOf(permitsPerUnit));
			return (Long.parseLong(val.toString()) > 0);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return false;
		}
	}

	private String getKeyNameForSecond(String keyPrefix) {
		// milliseconds
		Long redisServerTimeSecondUnit = redisUtils.getServerTimeInSeconds();
		String keyName = keyPrefix + ":" + redisServerTimeSecondUnit;
		return keyName;
	}

	private String[] getKeyNames(String keyPrefix) {
		String[] keyNames = null;
		if (timeUnit == TimeUnit.MINUTES) {
			long index = redisUtils.getServerTimeInSeconds() / 60;
			String keyName1 = keyPrefix + ":" + (index - 1);
			String keyName2 = keyPrefix + ":" + index;
			keyNames = new String[] { keyName1, keyName2 };
		} else if (timeUnit == TimeUnit.HOURS) {
			long index = redisUtils.getServerTimeInSeconds() / 3600;
			String keyName1 = keyPrefix + ":" + (index - 1);
			String keyName2 = keyPrefix + ":" + index;
			keyNames = new String[] { keyName1, keyName2 };
		} else if (timeUnit == TimeUnit.DAYS) {
			long index = redisUtils.getServerTimeInSeconds() / (3600 * 24);
			String keyName1 = keyPrefix + ":" + (index - 1);
			String keyName2 = keyPrefix + ":" + index;
			keyNames = new String[] { keyName1, keyName2 };
		} else {
			throw new java.lang.IllegalArgumentException("Don't support this TimeUnit: " + timeUnit);
		}
		return keyNames;
	}

}
