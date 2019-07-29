package com.fengxin58.limiter.redis;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.redis.connection.lettuce.LettuceConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.Assert;
import org.springframework.util.NumberUtils;

import io.lettuce.core.RedisFuture;
import io.lettuce.core.cluster.api.async.RedisClusterAsyncCommands;

public class RedisUtils {

	private RedisTemplate<?, ?> redisTemplate;

	public RedisUtils(RedisTemplate<?, ?> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}
	
	public Long getServerTimeInSeconds() {
    	Long redisServerTime = redisTemplate.getRequiredConnectionFactory().getConnection().time();
    	Long redisServerTimeSecondUnit = redisServerTime / 1000;
    	return redisServerTimeSecondUnit;
    }

	public Long getServerTimeInMicros() throws InterruptedException, ExecutionException {
		LettuceConnection redisConnection = (LettuceConnection) redisTemplate.getRequiredConnectionFactory()
				.getConnection();
		RedisClusterAsyncCommands<byte[], byte[]> connection = redisConnection.getNativeConnection();
		RedisFuture<List<byte[]>> time = connection.time();
		Long timeInMicros = BYTES_LIST_TO_TIME_CONVERTER.convert(time.get());
		return timeInMicros;
	}

	private Converter<byte[], String> BYTES_TO_STRING = source -> {
		if (source == null || Arrays.equals(source, new byte[0])) {
			return null;
		}
		return new String(source);
	};

	private Converter<List<byte[]>, Long> BYTES_LIST_TO_TIME_CONVERTER = source -> {

		Assert.notEmpty(source, "Received invalid result from server. Expected 2 items in collection.");
		Assert.isTrue(source.size() == 2,
				"Received invalid nr of arguments from redis server. Expected 2 received " + source.size());

		return toTimeMicros(toString(source.get(0)), toString(source.get(1)));
	};

	private String toString(byte[] source) {
		return BYTES_TO_STRING.convert(source);
	}

	private Long toTimeMicros(String seconds, String microseconds) {
		return NumberUtils.parseNumber(seconds, Long.class) * 1000L * 1000L
				+ NumberUtils.parseNumber(microseconds, Long.class);
	}

}
