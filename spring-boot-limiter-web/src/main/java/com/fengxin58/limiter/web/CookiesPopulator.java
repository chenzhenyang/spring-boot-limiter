package com.fengxin58.limiter.web;

import java.util.HashMap;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.springframework.expression.spel.support.StandardEvaluationContext;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CookiesPopulator implements IStandardEvaluationContextPopulator{

	@Override
	public void populate(HttpServletRequest request, StandardEvaluationContext context) {
		HashMap<String, String> cookieMap = new HashMap<>();
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                cookieMap.put(cookie.getName(), cookie.getValue());
            }
        }
        context.setVariable("Cookies", cookieMap);
        
        if(log.isDebugEnabled()) {
        	log.debug("Popluate StandardEvaluationContext with Cookies\n" + MapUtils.toString(cookieMap));
        }
	}

}
