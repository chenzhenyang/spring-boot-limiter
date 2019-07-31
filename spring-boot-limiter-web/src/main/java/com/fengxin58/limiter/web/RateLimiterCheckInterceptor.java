package com.fengxin58.limiter.web;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.http.HttpStatus;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.fengxin58.limiter.DynamicRateLimiter;
import com.fengxin58.limiter.LimiterConfig;
import com.fengxin58.limiter.RateLimiter;
import com.fengxin58.limiter.RateLimiterChecker;
import com.fengxin58.limiter.event.RateExceedingEvent;

public final class RateLimiterCheckInterceptor extends HandlerInterceptorAdapter implements ApplicationContextAware, InitializingBean {

	private String keyPrefix;

    private  RateLimiterChecker rateCheckTaskRunner;

    private  RedisLimiterConfigProcessor redisLimiterConfigProcessor;

    private ApplicationContext applicationContext;

    private String applicationName;
    
    private Collection<IStandardEvaluationContextPopulator> populators;
    
    
    public Collection<IStandardEvaluationContextPopulator> getPopulators() {
		return populators;
	}

	public void setPopulators(Collection<IStandardEvaluationContextPopulator> populators) {
		this.populators = populators;
	}

	public RateLimiterCheckInterceptor(RateLimiterChecker rateCheckTaskRunner,RedisLimiterConfigProcessor redisLimiterConfigProcessor) {
    	this.redisLimiterConfigProcessor = redisLimiterConfigProcessor;
    	this.rateCheckTaskRunner = rateCheckTaskRunner;
    	this.redisLimiterConfigProcessor = redisLimiterConfigProcessor;
    }
    
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
    @Override
    public void afterPropertiesSet(){
        applicationName = applicationContext.getEnvironment().getProperty("spring.application.name");
        if(applicationName == null) {
            throw new BeanInitializationException("the property with key 'spring.application.name' must be set!");
        }
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        boolean isSuccess = true;
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        if (method.isAnnotationPresent(RateLimiter.class)) {
            isSuccess = handleStatic(method, request, response);
        }
        else if(method.isAnnotationPresent(DynamicRateLimiter.class)) {
            isSuccess = handleDynamic(method, request, response);
        }
        return isSuccess;
    }

    private boolean handleStatic(Method method, HttpServletRequest request, HttpServletResponse response) throws Exception{
        RateLimiter rateLimiterAnnotation = method.getAnnotation(RateLimiter.class);
        int permits = rateLimiterAnnotation.permits();
        TimeUnit timeUnit = rateLimiterAnnotation.timeUnit();
        String path = rateLimiterAnnotation.path();
        if ("".equals(path)) {
            path = request.getRequestURI();
        }

        String baseExp = rateLimiterAnnotation.base();
        String baseVal = "";
        if(!"".equals(baseExp)) {
            baseVal = eval(baseExp, request);
        }
        String rateLimiterKey = keyPrefix + ":" + applicationName + ":" + path + ":" + baseVal;
        boolean isSuccess = rateCheckTaskRunner.check(rateLimiterKey, timeUnit, permits);
        
        if(!isSuccess) {
            rateExceeded(method, response, baseExp, baseVal, path, permits, timeUnit.name());
        }
        return isSuccess;
    }

    private boolean handleDynamic(Method method, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean isSuccess = true;
        String limiterConfigKey = method.getDeclaringClass().getSimpleName() + ":" + method.getName();
        LimiterConfig limiterConfig = redisLimiterConfigProcessor.get(limiterConfigKey);
        if(limiterConfig != null) {
            String baseExp = limiterConfig.getBaseExp();
            String baseVal = "";
            if(!"".equals(baseExp)) {
                baseVal = eval(baseExp, request);
            }
            String path = limiterConfig.getPath();
            if("".equals(path)) {
                path = request.getRequestURI();
            }
            int permits = limiterConfig.getPermits();
            String timeUnit = limiterConfig.getTimeUnit();
            String rateLimiterKey = keyPrefix + ":" + applicationName + ":" + path + ":" + baseVal;
            isSuccess = rateCheckTaskRunner.check(rateLimiterKey, TimeUnit.valueOf(timeUnit), permits);
            if(!isSuccess) {
                rateExceeded(method, response, baseExp, baseVal, path, permits, timeUnit);
            }
        }
        return isSuccess;
    }

    private void rateExceeded(Method method, HttpServletResponse response, String baseExp, String baseVal, String path, int permits, String timeUnit) throws Exception {
        buildDenyResponse(response);
        RateExceedingEvent rateExceedingEvent = new RateExceedingEvent();
        rateExceedingEvent.setApplicationName(applicationName);
        rateExceedingEvent.setControllerName(method.getDeclaringClass().getSimpleName());
        rateExceedingEvent.setMethodName(method.getName());
        rateExceedingEvent.setBaseExp(baseExp);
        rateExceedingEvent.setBaseValue(baseVal);
        rateExceedingEvent.setPath(path);
        rateExceedingEvent.setPermits(permits);
        rateExceedingEvent.setTimeUnit(timeUnit);
        applicationContext.publishEvent(rateExceedingEvent);
    }

    private String eval(String baseExp, HttpServletRequest request) {
        StandardEvaluationContext context = new StandardEvaluationContext();
        ExpressionParser expressionParser = new SpelExpressionParser();
        
        Collection<IStandardEvaluationContextPopulator> populators = getPopulators();
        
        populators.forEach(populator->{
        	populator.populate(request, context);
        });
        
        Expression expression = expressionParser.parseExpression(baseExp);
        String baseVal = expression.getValue(context, String.class);
        if(baseVal == null) {
            baseVal = "";
        }
        return baseVal;
    }

    private void buildDenyResponse(HttpServletResponse response) throws Exception{
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.getWriter().print("Access denied because of exceeding access rate limiter,please try again later");
    }

}
