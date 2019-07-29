package com.fengxin58.limiter;

public interface IRateLimiter {
	
	boolean acquire(String keyPrefix, int permitsPerUnit) throws AcquireException;
	
}
