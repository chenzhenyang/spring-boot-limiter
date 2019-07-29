package com.fengxin58.limiter.redis;

import java.util.concurrent.TimeUnit;

public class TimeUnitCalculator {
	
	private static final int PERIOD_SECOND_TTL = 10;
	private static final int PERIOD_MINUTE_TTL = 2 * 60 + 10;
	private static final int PERIOD_HOUR_TTL = 2 * 3600 + 10;
	private static final int PERIOD_DAY_TTL = 2 * 3600 * 24 + 10;

	private static final long MICROSECONDS_IN_MINUTE = 60 * 1000000;
	private static final long MICROSECONDS_IN_HOUR = 3600 * 1000000;
	private static final long MICROSECONDS_IN_DAY = 24 * 3600 * 1000000;

	private final TimeUnit timeUnit;

	public TimeUnitCalculator(TimeUnit timeUnit) {
		this.timeUnit = timeUnit;
	}

	public int getExpire() {
		int expire = 0;
		if (timeUnit == TimeUnit.SECONDS) {
			expire = PERIOD_SECOND_TTL;
		} else if (timeUnit == TimeUnit.MINUTES) {
			expire = PERIOD_MINUTE_TTL;
		} else if (timeUnit == TimeUnit.HOURS) {
			expire = PERIOD_HOUR_TTL;
		} else if (timeUnit == TimeUnit.DAYS) {
			expire = PERIOD_DAY_TTL;
		} else {
			throw new java.lang.IllegalArgumentException("Don't support this TimeUnit: " + timeUnit);
		}
		return expire;
	}

	public long getPeriodMicrosecond() {
		if (timeUnit == TimeUnit.MINUTES) {
			return MICROSECONDS_IN_MINUTE;
		} else if (timeUnit == TimeUnit.HOURS) {
			return MICROSECONDS_IN_HOUR;
		} else if (timeUnit == TimeUnit.DAYS) {
			return MICROSECONDS_IN_DAY;
		} else {
			throw new java.lang.IllegalArgumentException("Don't support this TimeUnit: " + timeUnit);
		}
	}
}
