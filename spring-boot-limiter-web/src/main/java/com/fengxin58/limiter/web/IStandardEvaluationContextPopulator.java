package com.fengxin58.limiter.web;

import javax.servlet.http.HttpServletRequest;

import org.springframework.expression.spel.support.StandardEvaluationContext;

public interface IStandardEvaluationContextPopulator {
	
	public void populate(HttpServletRequest request,StandardEvaluationContext context);
	
}
