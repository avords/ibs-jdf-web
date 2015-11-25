package com.handpay.ibenefit.framework.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.handpay.ibenefit.framework.config.GlobalConfig;
import com.handpay.ibenefit.security.SecurityConstants;

public final class CookieUtils {
	private static final Logger LOGGER = Logger.getLogger(CookieUtils.class);

	private CookieUtils() {
	}

	public static String getEncodedCookieByName(Cookie[] userCookies, String cookieName) {
		if (cookieName == null || userCookies == null || userCookies.length == 0) {
			return null;
		}
		for (Cookie cookie : userCookies) {
			if (cookieName.equals(cookie.getName())) {
				String val = cookie.getValue();
				if (StringUtils.isNotBlank(val)) {
					try {
						val = new String(Hex.decodeHex(val.toCharArray()));
					} catch (DecoderException e) {
						LOGGER.debug("getEncodedCookieByName",e);
					}
				}

				return val;
			}
		}
		return null;
	}

	public static String getCookieByName(Cookie[] userCookies, String cookieName) {
		if (cookieName == null || userCookies == null || userCookies.length == 0) {
			return null;
		}
		for (Cookie cookie : userCookies) {
			if (cookieName.equals(cookie.getName()) && StringUtils.isNotBlank(cookie.getValue())) {
				return cookie.getValue();
			}
		}
		return null;
	}
	
	public static void setUserTokenCookie(HttpServletResponse response, String token,int maxAge) {
		setEncodedCookie(response, SecurityConstants.SECURITY_TOKEN, token, maxAge);
	}

	public static void setUserTokenCookie(HttpServletResponse response, String token) {
		setEncodedCookie(response, SecurityConstants.SECURITY_TOKEN, token);
	}

	public static String getUserTokenFromCookie(Cookie[] userCookies) {
		return getEncodedCookieByName(userCookies, SecurityConstants.SECURITY_TOKEN);
	}

	public static void addCookies(HttpServletResponse response, Cookie cookie) {
		response.addCookie(cookie);
	}

	public static void setEncodedCookie(HttpServletResponse response, String name, String value) {
		Cookie cookie = new Cookie(name, new String(Hex.encodeHex(value.getBytes())));
		addCookie(response, cookie);
	}
	
	public static void setEncodedCookie(HttpServletResponse response, String name, String value,int maxAge) {
		Cookie cookie = new Cookie(name, new String(Hex.encodeHex(value.getBytes())));
		addCookie(response, cookie, maxAge);
	}

	public static void setCookie(HttpServletResponse response, String name, String value) {
		Cookie cookie = new Cookie(name, value);
		addCookie(response, cookie,30*24*3600);
	}

	private static void addCookie(HttpServletResponse response, Cookie aCookie,int maxAge) {
		aCookie.setMaxAge(maxAge);
		addCookie(response, aCookie);
	}

	private static void addCookie(HttpServletResponse response, Cookie aCookie) {
		if(StringUtils.isNotBlank(GlobalConfig.getCookieDomain())){
			aCookie.setDomain(GlobalConfig.getCookieDomain());
		}
		aCookie.setPath(GlobalConfig.getCookiePath());
		response.addCookie(aCookie);
	}
	
	public static void deleteCookie(HttpServletResponse response,String name){
        Cookie deleted = new Cookie(name, null);
        deleted.setMaxAge(0);
        deleted.setDomain(GlobalConfig.getCookieDomain());
        deleted.setPath(GlobalConfig.getCookiePath());
        response.addCookie(deleted);
	}

}
