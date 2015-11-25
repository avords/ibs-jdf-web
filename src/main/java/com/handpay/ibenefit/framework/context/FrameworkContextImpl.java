package com.handpay.ibenefit.framework.context;

import com.handpay.ibenefit.security.entity.User;

public class FrameworkContextImpl implements FrameworkContext {
	private static final long	serialVersionUID	= 936374620512271312L;
	private User currentUser;
	public User getCurrentUser() {
		return currentUser;
	}
	public void setCurrentUser(User user) {
		this.currentUser = user;
	}
}
