package com.handpay.ibenefit.framework.web;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import com.handpay.ibenefit.framework.cache.ICacheManager;
import com.handpay.ibenefit.framework.session.HttpServletRequestWrapper;

public class CachedSessionFilter implements Filter {

	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
	        throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		String seesionId = request.getSession().getId();
		ICacheManager cacheManager = FrameworkFactory.getCacheManager();
		//如果不能连上集群的缓存服务器，则使用容器自带的的存储机制，此时会话不能做失败转移
		if(cacheManager!=null && cacheManager.test()){
			filterChain.doFilter(new HttpServletRequestWrapper(seesionId, request, cacheManager), servletResponse);
		}
	}

	public void init(FilterConfig filterConfig) throws ServletException {
	}

	public void destroy() {
	}
}
