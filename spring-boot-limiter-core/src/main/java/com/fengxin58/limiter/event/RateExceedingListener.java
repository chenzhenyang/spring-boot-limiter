package com.fengxin58.limiter.event;

import org.springframework.context.ApplicationListener;

public interface RateExceedingListener extends ApplicationListener<RateExceedingEvent> {
}
