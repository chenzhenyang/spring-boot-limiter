package com.fengxin58.limiter.web;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

public class AuthenticationPopulator implements IStandardEvaluationContextPopulator{

	@Override
	public void populate(HttpServletRequest request, StandardEvaluationContext context) {
		Map<String,Object> map = new HashMap<>();
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if(authentication==null) {
			return ;
		}
		
		if(authentication instanceof OAuth2Authentication) {
			Object principal = authentication.getPrincipal();
			String clientId = ((OAuth2Authentication)authentication).getOAuth2Request().getClientId();
			map.put("principal", principal.toString());
			map.put("client", clientId);
			context.setVariable("AccessToken", map);
		}
		
		//TODO other type Authentication
			
	}

}