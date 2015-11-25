package com.handpay.ibenefit.framework.util;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.extremecomponents.table.context.Context;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import com.handpay.ibenefit.framework.ProjectConfig;

public final class LocaleUtils {
	public static final String DEFAULT_LOCALE_PARAM_NAME = "locale";
	
	private LocaleUtils(){
	}
	
	public static Locale getLocale(String localeName){
		Locale result = null;
		if(StringUtils.isNotBlank(localeName)){
			 String parts[] = StringUtils.split(localeName, "_");
		        String language = parts[0];
		        if(parts.length == 2){
		            String country = parts[1];
		            result = new Locale(language, country);
		        }
		}
        if(result==null){
        	result = getDefaultLocale();
		}
        return result;
	}
	
	public static void setSpringLocale(HttpSession session,Locale locale){
		session.setAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME, locale);
	}
	
	public static void setExtremetableLocale(HttpSession session,String localeName){
		session.setAttribute(LocaleUtils.DEFAULT_LOCALE_PARAM_NAME, localeName);
	}
	
	public static Locale getLocale(HttpSession session){
		Locale locale = (Locale)session.getAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
		if(locale==null){
			locale = getDefaultLocale();
		}
		return locale;
	}

	public static Locale getDefaultLocale() {
	    Locale locale = ProjectConfig.getDetaultLocale();
	    if(locale==null){
	    	locale = Locale.getDefault();
	    }
	    return locale;
    }
	
	public static Locale getLocale(HttpServletRequest request){
		return getLocale(request.getSession());
	}
	
	public static Locale getLocale(Context context){
		Locale locale = (Locale)context.getSessionAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
		if(locale==null){
			locale = getDefaultLocale();
		}
		return locale;
	}
	
}
