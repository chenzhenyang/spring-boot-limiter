package com.fengxin58.limiter;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Retention(RUNTIME)
@Target({ METHOD })
public @interface RateLimiter {

    String base() default "";

    String path() default "";

    TimeUnit timeUnit() default TimeUnit.SECONDS;

    int permits() default 10000;
}
