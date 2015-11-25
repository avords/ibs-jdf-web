package com.handpay.ibenefit.framework.context;


public final class ThreadLocalContextHolder {
	private ThreadLocalContextHolder() {
	}

	private static ThreadLocal contextHolder = new ThreadLocal();

	public static void clearContext() {
		contextHolder.set(null);
	}

	public static FrameworkContext getContext() {
		if (contextHolder.get() == null){
			contextHolder.set(new FrameworkContextImpl());
		}
		return (FrameworkContext) contextHolder.get();
	}

	public static void setContext(FrameworkContext context) {
		contextHolder.set(context);
	}

}
