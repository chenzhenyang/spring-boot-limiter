package com.fengxin58.limiter.web;

import java.util.Enumeration;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.springframework.expression.spel.support.StandardEvaluationContext;

public class HeadersPopulator implements IStandardEvaluationContextPopulator{

	@Override
	public void populate(HttpServletRequest request, StandardEvaluationContext context) {
		 HashMap<String, String> headerMap = new HashMap();
	        Enumeration<String> headerNames = request.getHeaderNames();
	        if (headerNames != null) {
	            while (headerNames.hasMoreElements()) {
	                String headerName = headerNames.nextElement();
	                headerMap.put(headerName, request.getHeader(headerName));
	            }
	        }
	        context.setVariable("Headers", headerMap);
		
	}

}
