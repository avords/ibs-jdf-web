package com.handpay.ibenefit.framework.web;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.handpay.ibenefit.framework.FrameworkConstants;

public final class MessageUtils {

	public static String getMessage(String message) {
		return "?" + FrameworkConstants.MESSAGE + "=" + urlEncodeWithUtf8(message);
	}

	public static String urlEncodeWithUtf8(String original) {
		if(original==null||original.length()==0){
			return original;
		}
		String result = original;
		try {
			result = URLEncoder.encode(original, "UTF-8");
		} catch (UnsupportedEncodingException e) {
		}
		return result;
	}

}