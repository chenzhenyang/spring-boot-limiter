package com.fengxin58.limiter.web;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fengxin58.limiter.DynamicRateLimiter;
import com.fengxin58.limiter.LimiterConfig;


public final class RedisLimiterConfigProcessor  implements ApplicationContextAware, BeanPostProcessor, InitializingBean {
    private static Logger logger = LoggerFactory.getLogger(RedisLimiterConfigProcessor.class);


    private String applicationName;
    
    private boolean enableDynamicalConf;

    private ApplicationContext applicationContext;

    private ConcurrentHashMap<String, LimiterConfig> configMap = new ConcurrentHashMap<>();

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
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class clazz = bean.getClass();
        if(clazz.isAnnotationPresent(RestController.class) || clazz.isAnnotationPresent(Controller.class)) {
            Method[] methods = clazz.getDeclaredMethods();
             for (Method method : methods) {
                int modifiers = method.getModifiers();
                if(Modifier.isPublic(modifiers) && method.isAnnotationPresent(DynamicRateLimiter.class)) {
                    if(!enableDynamicalConf) {
                        throw new RuntimeException("Must set spring.redis-limiter.enable-dynamical-conf = true, then you can use DynamicRateLimiter annotation.");
                    }
                    DynamicRateLimiter dynamicRateLimiter = method.getAnnotation(DynamicRateLimiter.class);
                    int permits = dynamicRateLimiter.permits();
                    TimeUnit timeUnit = dynamicRateLimiter.timeUnit();
                    String path = dynamicRateLimiter.path();
                    String baseExp = dynamicRateLimiter.base();
                    LimiterConfig config = new LimiterConfig();
                    config.setApplicationName(applicationName);
                    config.setBaseExp(baseExp);
                    config.setPath(path);
                    config.setPermits(permits);
                    config.setTimeUnit(timeUnit.name());
                    config.setControllerName(clazz.getSimpleName());
                    config.setMethodName(method.getName());
                    String key = clazz.getSimpleName()+":"+method.getName();
                    if(configMap.containsKey(key)) {
                        throw new RuntimeException(String.format("Controller %s method %s has conflict.", clazz.getSimpleName(), method.getName()));
                    }
                    configMap.put(key, config);
                }
            }
        }
        return bean;
    }


    //TODO JedisPubSub
    public void onMessage(String channel, String message) {
        ObjectMapper objectMapper = new ObjectMapper();
        LimiterConfig config = null;
        try {
            config = objectMapper.readValue(message, LimiterConfig.class);
        }
        catch(IOException e) {
            logger.error("read config from message failed. the message content is " + message);
        }
        if(config != null) {
            if (applicationName.equals(config.getApplicationName())) {
                String key = config.getControllerName() + ":" + config.getMethodName();
                synchronized(this) {
                    if (config.isDeleted()) {
                        configMap.remove(key);
                    } else {
                        configMap.put(key, config);
                    }
                }
            }
        }
    }

    public synchronized LimiterConfig get(String key) {
        return configMap.get(key);
    }

}
