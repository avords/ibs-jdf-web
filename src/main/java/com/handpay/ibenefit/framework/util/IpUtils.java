package com.handpay.ibenefit.framework.util;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

public final class IpUtils {

	public static String getRealIp(HttpServletRequest request){
		String ip = request.getHeader("X-Real-IP");
		if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("X-Forwarded-For");
		} else {
			return ip;
		}
		if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		} else {
			int index = ip.indexOf(',');
			if (index != -1) {
				ip = ip.substring(0, index);
			}
		}
		return ip;
	}

}
