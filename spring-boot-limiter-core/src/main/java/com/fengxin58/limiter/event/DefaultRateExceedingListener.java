package com.fengxin58.limiter.event;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class DefaultRateExceedingListener implements  RateExceedingListener {

    @Override
    public void onApplicationEvent(RateExceedingEvent event) {
    	if(log.isWarnEnabled()) {
    		log.warn("Rate exceeding event arise: " + event.toString());	
    	}
    }
}
