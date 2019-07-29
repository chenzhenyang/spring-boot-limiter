package com.fengxin58.limiter;

public class AcquireException extends RuntimeException{

	public AcquireException(Exception e) {
		super(e);
	}

}
