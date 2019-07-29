package com.fengxin58.limiter.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DefaultRateExceedingListener implements  RateExceedingListener {
    private static Logger logger = LoggerFactory.getLogger(DefaultRateExceedingListener.class);

    @Override
    public void onApplicationEvent(RateExceedingEvent event) {
        logger.warn("Rate exceeding event arise: " + event.toString());
    }
}
