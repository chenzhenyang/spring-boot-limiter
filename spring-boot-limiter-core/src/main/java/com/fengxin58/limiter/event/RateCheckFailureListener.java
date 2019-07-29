package com.fengxin58.limiter.event;

import org.springframework.context.ApplicationListener;

public interface RateCheckFailureListener extends ApplicationListener<RateCheckFailureEvent> {

}
