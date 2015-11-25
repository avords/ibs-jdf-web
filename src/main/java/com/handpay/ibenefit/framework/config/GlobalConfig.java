package com.handpay.ibenefit.framework.config;

import org.apache.commons.lang3.StringUtils;


/**
 * Global configuration across whole domain
 * @author pubx
 *
 */
public class GlobalConfig {
	private static final String DEFAULT_COOKIE_PATH = "/";
	private static final String DEFAULT_SESSION_NAME = "sid";
	private static final String DEFAULT_COOKIE_DOMAIN = "localhost";
	private static final String DEFAULT_LOGIN_URL = "/jdf/login";
	private static final String DEFAULT_TEMP_DIR = System.getProperty("java.io.tmpdir");
	private static final int DEFAULT_REFRESH_PERMISSION_TIME  = 10;
	private static final int DEFAULT_LOGIN_ERROR_WAIT_TIME = 30;
	private static String sessionName = DEFAULT_SESSION_NAME;
	private static String cookieDomain = DEFAULT_COOKIE_DOMAIN;
	private static String cookiePath = DEFAULT_COOKIE_PATH;
	private static int refreshPermissionTime = DEFAULT_REFRESH_PERMISSION_TIME;
	private static String rootUrl;
	private static String loginUrl = DEFAULT_LOGIN_URL;
	private static boolean singleLogin;
	private static int loginErrorWaitTime = DEFAULT_LOGIN_ERROR_WAIT_TIME;
	private static String accessControlAll = "allow";
	private static boolean accessAll = true;
	private static String baseFilePath;
	//工程的静态资源文件域名
	private static String staticDomain;
	//工程的页面访问域名
	private static String dynamicDomain;
	//运营端上传文件的下载域名
	private static String adminStaticDomain;
	//支付接口的域名
    private static String payDomain;
    //其它加密域名
    private static String secureDomain;
    
    private static String tempDir = DEFAULT_TEMP_DIR;

	public static String getSessionName() {
		return sessionName;
	}
	public void setSessionName(String sessionName) {
		GlobalConfig.sessionName = sessionName;
	}
	public static String getCookieDomain() {
		return cookieDomain;
	}
	public void setCookieDomain(String domain) {
		GlobalConfig.cookieDomain = domain;
	}
	public static String getCookiePath() {
		return cookiePath;
	}
	public void setCookiePath(String cookiePath) {
		GlobalConfig.cookiePath = cookiePath;
	}
	public static String getLoginUrl() {
    	return rootUrl + loginUrl;
    }
	public void setLoginUrl(String loginUrl) {
    	GlobalConfig.loginUrl = loginUrl;
    }
	public static int getRefreshPermissionTime() {
    	return refreshPermissionTime;
    }
	public void setRefreshPermissionTime(int refreshPermissionTime) {
    	GlobalConfig.refreshPermissionTime = refreshPermissionTime;
    }
	public static String getRootUrl() {
		if(null==rootUrl){
			rootUrl = "";
			//throw new IllegalArgumentException("rootUrl can not be null");
		}
    	return rootUrl;
    }
	public void setRootUrl(String rootUrl) {
    	GlobalConfig.rootUrl = rootUrl;
    }
	public static boolean isSingleLogin() {
    	return singleLogin;
    }
	public void setSingleLogin(boolean singleLogin) {
    	GlobalConfig.singleLogin = singleLogin;
    }
	public static int getLoginErrorWaitTime() {
    	return loginErrorWaitTime;
    }
	public void setLoginErrorWaitTime(int loginErrorWaitTime) {
    	GlobalConfig.loginErrorWaitTime = loginErrorWaitTime;
    }
	public static String getAccessControlAll() {
		return accessControlAll;
	}
	public void setAccessControlAll(String accessControlAll) {
		if(accessControlAll!=null){
			GlobalConfig.accessControlAll = accessControlAll;
			if(accessControlAll.equals("allow")){
				accessAll = true;
			}else if(accessControlAll.equals("deny")){
				accessAll = false;
			}
		}
	}

	public static boolean isAccessAll() {
		return accessAll;
	}
	public static String getBaseFilePath() {
		return baseFilePath;
	}

	public void setBaseFilePath(String baseFilePath) {
		GlobalConfig.baseFilePath = baseFilePath;
	}
	public static String getAdminStaticDomain() {
		return adminStaticDomain;
	}
	public void setAdminStaticDomain(String adminStaticDomain) {
		GlobalConfig.adminStaticDomain = adminStaticDomain;
	}
	public static String getStaticDomain() {
		return staticDomain;
	}
	public void setStaticDomain(String staticDomain) {
		GlobalConfig.staticDomain = staticDomain;
	}
	public static String getDynamicDomain() {
		return dynamicDomain;
	}
	public void setDynamicDomain(String dynamicDomain) {
		GlobalConfig.dynamicDomain = dynamicDomain;
	}
	public static String getPayDomain() {
        return GlobalConfig.payDomain;
    }
    public void setPayDomain(String payDomain) {
        GlobalConfig.payDomain = payDomain;
    }
	public static String getSecureDomain() {
		return secureDomain;
	}
	public void setSecureDomain(String secureDomain) {
		GlobalConfig.secureDomain = secureDomain;
	}
	public static String getTempDir() {
		return tempDir;
	}
	public void setTempDir(String tempDir) {
		if(StringUtils.isNotBlank(tempDir)){
			GlobalConfig.tempDir = tempDir;
		}
	}
	
}
