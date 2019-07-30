package com.fengxin58.limiter;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.fengxin58.limiter.event.RateCheckFailureEvent;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class RateChecker implements ApplicationContextAware {
	
	private ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

	private final IRateLimiterFactory rateLimiterFactory;


	public RateChecker(IRateLimiterFactory redisRateLimiterFactory) {
		this.rateLimiterFactory = redisRateLimiterFactory;

	}

	private ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	public boolean checkRun(String rateLimiterKey, TimeUnit timeUnit, int permits) {
		CheckTask task = new CheckTask(rateLimiterKey, timeUnit, permits);
		Future<Boolean> checkResult = executorService.submit(task);
		boolean retVal = true;
		try {
			//TODO checkActionTimeout
			retVal = checkResult.get(100, TimeUnit.MILLISECONDS);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			applicationContext.publishEvent(new RateCheckFailureEvent(e, "Access rate check task executed failed."));
		} 
		return retVal;
	}

	class CheckTask implements Callable<Boolean> {
		private String rateLimiterKey;
		private TimeUnit timeUnit;
		private int permits;

		CheckTask(String rateLimiterKey, TimeUnit timeUnit, int permits) {
			this.rateLimiterKey = rateLimiterKey;
			this.timeUnit = timeUnit;
			this.permits = permits;
		}

		public Boolean call() throws InterruptedException, ExecutionException {
			IRateLimiter redisRatelimiter = rateLimiterFactory.get(timeUnit);
			return redisRatelimiter.acquire(rateLimiterKey, permits);
		}
	}
}
