package com.fengxin58.limiter;

/**
 * 
 * @author chenzhenyang
 *
 */
public interface IRateLimiter {
	
	/**
	 * 
	 * @param key
	 * @param permitsPerUnit
	 * @return
	 * @throws AcquireException
	 */
	boolean acquire(String key, int permitsPerUnit);
	
}
