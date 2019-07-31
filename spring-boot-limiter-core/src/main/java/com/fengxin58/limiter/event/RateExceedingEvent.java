package com.fengxin58.limiter.event;

import org.springframework.context.ApplicationEvent;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public final class RateExceedingEvent extends ApplicationEvent {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static Object dummy = new Object();
    private String applicationName;
    private String controllerName;
    private String methodName;
    private String baseExp;
    private String baseValue;
    private String path;
    private String timeUnit;
    private int permits;
    public RateExceedingEvent() {
        super(dummy);
    }
}
