package com.handpay.ibenefit.framework.util;

import com.handpay.ibenefit.framework.ProjectConfig;
import com.handpay.ibenefit.framework.config.GlobalConfig;

public final class DomainUtils {
	private DomainUtils() {
	}

	private static final String DYNAMIC_DOMAIN;
	public static final String STATIC_ROOT = "static";
	static {
		if(ProjectConfig.ROOT_PROJECT.equalsIgnoreCase(ProjectConfig.PROJECT_NAME)){
			DYNAMIC_DOMAIN = GlobalConfig.getRootUrl();
		}else{
			DYNAMIC_DOMAIN = GlobalConfig.getRootUrl() + "/" + ProjectConfig.PROJECT_NAME;
		}
	}

	@Deprecated
	public static String getStaticDomain() {
		return GlobalConfig.getStaticDomain();
	}

	@Deprecated
	public static String getMainStaticDomain() {
		return GlobalConfig.getStaticDomain();
	}

	public static String getDynamicDomain() {
		return GlobalConfig.getDynamicDomain();
	}
}
