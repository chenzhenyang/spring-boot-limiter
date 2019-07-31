package com.fengxin58.limiter.web;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import lombok.extern.slf4j.Slf4j;

@Slf4j
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
			map.put("user_name", principal.toString());
			map.put("client_id", clientId);
			context.setVariable("JwtClaims", map);
			
			if(log.isDebugEnabled()) {
	        	log.debug("Popluate StandardEvaluationContext with OAuth2Authentication\n" + MapUtils.toString(map));
	        }
		}
		
		//TODO other type Authentication
			
	}

}
