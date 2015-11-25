package com.handpay.ibenefit.framework.util;

import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

public class MessageUtils {
	private static final Logger LOGGER = Logger.getLogger(MessageUtils.class);

	private static final Map<String, Map<String, String>> LOCAL_CACHE = new ConcurrentHashMap<String, Map<String, String>>();

	private MessageUtils() {
	}

	private static final String DEFAULT_RESOURCE_FILE_BASE_NAME = "messages";

	private static final Map<String, ResourceBundle> BINDS = new ConcurrentHashMap<String, ResourceBundle>();

	private static ResourceBundle getBind(String locale) {
		if (locale == null || locale.equals("")) {
			locale = LocaleUtils.getDefaultLocale().toString();
		}
		ResourceBundle bind = BINDS.get(locale);
		if (bind == null) {
			bind = ResourceBundle.getBundle(DEFAULT_RESOURCE_FILE_BASE_NAME + "_" + locale);
			BINDS.put(locale, bind);
		}
		return bind;
	}

	public final static String getMessage(String key, Locale locale) {
		if (locale == null) {
			locale = LocaleUtils.getDefaultLocale();
		}
		String message = getMessageFromBundle(key, locale);
		if (StringUtils.isEmpty(message)) {
			return key;
		} else {
			return message;
		}
	}


	protected static String getMessageFromBundle(String key, Locale locale) {
		String message = null;
		try {
			ResourceBundle resourceBundle = getBind(locale.toString());
			message = resourceBundle.getString(key);
		} catch (Exception e) {
			LOGGER.debug("Resource not fuound:" + key + " of " + locale);
		}
		return message;
	}

	public final static String getMessage(String key, Object[] values, Locale locale) {
		String message = getMessage(key, locale);
		if (values != null) {
			for (int i = 0; i < values.length; i++) {
				message = StringUtils.replace(message, "{" + i + "}", values[i].toString());
			}
		}
		return message;
	}


	public final static void clear() {
		LOCAL_CACHE.clear();
	}

	public final static String getMessage(String key, HttpServletRequest request) {
		Locale locale = LocaleUtils.getLocale(request);
		return getMessage(key, locale);
	}

	public final static String getMessage(String key, String[] values, HttpServletRequest request) {
		Locale locale = LocaleUtils.getLocale(request);
		return getMessage(key, values, locale);
	}

	@Deprecated
	public static String getMessage(String key) {
		String message = null;
		try {
			message = getBind(null).getString(key);
		} catch (Exception e) {
		}
		if (StringUtils.isEmpty(message)) {
			return key;
		} else {
			return message;
		}
	}

	public static String getMessage(String key, String[] values) {
		String message = getMessage(key);
		if (values != null) {
			for (int i = 0; i < values.length; i++) {
				message = StringUtils.replace(message, "{" + i + "}", values[i]);
			}
		}
		return message;
	}
	
	public static String getMessage(String key, HttpServletRequest request, String[] values) {
		String message = getMessage(key,request);
		if (values != null) {
			for (int i = 0; i < values.length; i++) {
				message = StringUtils.replace(message, "{" + i + "}", values[i]);
			}
		}
		return message;
	}

	public static void main(String[] args) {
		// AppBaseException a = new AppBaseException("errors.required",new
		System.out.println(getMessage("ok", new String[] { "HELLO" }));
	}

}
