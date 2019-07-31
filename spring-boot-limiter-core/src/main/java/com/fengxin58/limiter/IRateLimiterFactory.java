package com.fengxin58.limiter;

import java.util.concurrent.TimeUnit;

public  interface IRateLimiterFactory {

    public IRateLimiter get(TimeUnit timeUnit) ;
}
