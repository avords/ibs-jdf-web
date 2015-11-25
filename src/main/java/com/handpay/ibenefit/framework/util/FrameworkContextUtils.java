package com.handpay.ibenefit.framework.util;

import com.handpay.ibenefit.framework.context.ThreadLocalContextHolder;
import com.handpay.ibenefit.security.entity.User;

public final class FrameworkContextUtils {
	public static final String SYSTEM_NAME = "SYSTEM";
	public static final long SYSTEM_ID = -1;
	public static final int ADMIN_ID = 0;

	private FrameworkContextUtils() {
	}

	public static String getCurrentLoginName() {
		User user = ThreadLocalContextHolder.getContext().getCurrentUser();
		if (null ==user) {
			return SYSTEM_NAME;
		}
		return user.getLoginName();
	}
	
	public static User getCurrentUser(){
		return ThreadLocalContextHolder.getContext().getCurrentUser();
	}
	
	public static boolean isAdmin(){
		Long userId = getCurrentUserId();
		return userId!=null && userId == ADMIN_ID;
	}
	
	public static Long getCurrentUserId() {
		User user = ThreadLocalContextHolder.getContext().getCurrentUser();
		if (null==user) {
			return SYSTEM_ID;
		}
		return user.getObjectId();
	}

	public static String getCurrentUserName() {
		User user = ThreadLocalContextHolder.getContext().getCurrentUser();
		if (null==user) {
			return SYSTEM_NAME;
		}
		return user.getUserName();
	}
}
