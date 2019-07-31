package com.fengxin58.sample;

import org.springframework.stereotype.Component;

import com.fengxin58.limiter.event.RateCheckFailureEvent;
import com.fengxin58.limiter.event.RateCheckFailureListener;

@Component
public class MyCheckFailureListener implements RateCheckFailureListener {
    public void onApplicationEvent(RateCheckFailureEvent event) {
        System.out.println("my check ####" + event.getMsg());
    }
}
