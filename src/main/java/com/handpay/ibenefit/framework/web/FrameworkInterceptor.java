package com.handpay.ibenefit.framework.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.handpay.ibenefit.security.service.IAuthorizationManager;

public class FrameworkInterceptor extends HandlerInterceptorAdapter {

	private IAuthorizationManager authorizationManager;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		// intercept
		HttpSession session = request.getSession();
		if (session.getAttribute("user") == null) {
			return false;
		} else {
			return true;
		}
	}

	public IAuthorizationManager getAuthorizationManager() {
		return authorizationManager;
	}

	public void setAuthorizationManager(IAuthorizationManager authorizationManager) {
		this.authorizationManager = authorizationManager;
	}
	
}
