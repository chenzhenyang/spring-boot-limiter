package com.fengxin58.limiter;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.fengxin58.limiter.event.RateCheckFailureEvent;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class RateLimiterChecker implements ApplicationContextAware {

	private ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

	private final IRateLimiterFactory rateLimiterFactory;

	private ApplicationContext applicationContext;

	/**
	 * check action execution timeout(MILLISECONDS)
	 */
	private long checkActionTimeout = 10000;

	public RateLimiterChecker(IRateLimiterFactory rateLimiterFactory, long checkActionTimeout) {
		this.rateLimiterFactory = rateLimiterFactory;
		this.checkActionTimeout = checkActionTimeout;

	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	public boolean check(String rateLimiterKey, TimeUnit timeUnit, int permits) {
		CheckTask task = new CheckTask(rateLimiterKey, timeUnit, permits);
		Future<Boolean> checkResult = executorService.submit(task);
		boolean retVal = false;
		try {
			retVal = checkResult.get(checkActionTimeout, TimeUnit.MILLISECONDS);
		} catch (Exception e) {
			applicationContext.publishEvent(new RateCheckFailureEvent(e, "Access rate check task executed failed."));
			log.error(e.getMessage(), e);
		}
		return retVal;
	}

	private class CheckTask implements Callable<Boolean> {
		private String rateLimiterKey;
		private TimeUnit timeUnit;
		private int permits;

		CheckTask(String rateLimiterKey, TimeUnit timeUnit, int permits) {
			this.rateLimiterKey = rateLimiterKey;
			this.timeUnit = timeUnit;
			this.permits = permits;
		}

		public Boolean call() {
			IRateLimiter redisRatelimiter = rateLimiterFactory.get(timeUnit);
			return redisRatelimiter.acquire(rateLimiterKey, permits);
		}
	}
}
