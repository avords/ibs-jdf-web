package com.handpay.ibenefit.framework.web;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.handpay.ibenefit.framework.cache.ICacheManager;
import com.handpay.ibenefit.framework.config.GlobalConfig;
import com.handpay.ibenefit.framework.session.HttpServletRequestWrapper;
import com.handpay.ibenefit.framework.util.CookieUtils;

public class CookieCachedSessionFilter implements Filter {
	
	private String sessionName;
	private String cookieDomain;
	private String cookiePath;

	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
	        throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;
		String seesionId = CookieUtils.getCookieByName(request.getCookies(), sessionName);
		if (StringUtils.isBlank(seesionId)) {
			seesionId = java.util.UUID.randomUUID().toString();
			seesionId = seesionId.replace("-", "");
			Cookie sessionIdCookie = new Cookie(sessionName, seesionId);
			sessionIdCookie.setMaxAge(-1);
			//sessionIdCookie.setHttpOnly(true); //Servlet 3.0
			if (StringUtils.isNotBlank(cookieDomain)) {
				sessionIdCookie.setDomain(cookieDomain);
			}
			if(StringUtils.isNotBlank(cookiePath)){
				sessionIdCookie.setPath(cookiePath);
			}else{
				sessionIdCookie.setPath("/");
			}
			response.addCookie(sessionIdCookie);
		}
		ICacheManager cacheManager = FrameworkFactory.getCacheManager();
		//如果不能连上集群的缓存服务器，则使用容器自带的的存储机制，此时会话不能做失败转移
		if(cacheManager!=null && cacheManager.test()){
			filterChain.doFilter(new HttpServletRequestWrapper(seesionId, request, cacheManager), servletResponse);
		}else{
			filterChain.doFilter(servletRequest, servletResponse);
		}
	}

	public void init(FilterConfig filterConfig) throws ServletException {
		this.sessionName = GlobalConfig.getSessionName();
		this.cookieDomain = GlobalConfig.getCookieDomain();
		this.cookiePath = GlobalConfig.getCookiePath();
	}

	public static void main(String[] args) {
		
	}
	public void destroy() {
	}
}
