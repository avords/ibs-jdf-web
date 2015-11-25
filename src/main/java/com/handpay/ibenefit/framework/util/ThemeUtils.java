package com.handpay.ibenefit.framework.util;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;

import com.handpay.ibenefit.framework.FrameworkConstants;
import com.handpay.ibenefit.framework.ProjectConfig;
import com.handpay.ibenefit.framework.config.GlobalConfig;

public final class ThemeUtils {
	private ThemeUtils() {
	}

	public static String getFullCssThemePath(HttpSession session){
		String pathColor = (String) session.getAttribute(FrameworkConstants.SKIN);
		if (StringUtils.isEmpty(pathColor)) {
			pathColor = ProjectConfig.getDefaultSkin();
			session.setAttribute(FrameworkConstants.SKIN, pathColor);
		}
		return GlobalConfig.getStaticDomain() + FrameworkConstants.STYLE_ROOT + pathColor + "/";
	}
}
