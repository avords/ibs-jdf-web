package com.handpay.ibenefit.framework.session;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.handpay.ibenefit.framework.cache.ICacheManager;

public class HttpServletRequestWrapper extends javax.servlet.http.HttpServletRequestWrapper {

	private String sid = "";
	private ICacheManager cacheService;
	public HttpServletRequestWrapper(String sid, HttpServletRequest httpServletRequest,ICacheManager cacheService) {
		super(httpServletRequest);
		this.sid = sid;
		this.cacheService = cacheService;
	}

	public HttpSession getSession(boolean create) {
		return new HttpSessionSidWrapper(this.sid, super.getSession(create),cacheService);
	}

	public HttpSession getSession() {
		return new HttpSessionSidWrapper(this.sid, super.getSession(),cacheService);
	}

}
