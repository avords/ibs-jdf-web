package com.handpay.ibenefit.framework.exception;

/**
 * 
 * Framework Exception
 * @time: 2009-7-16 05:34:52
 * @author pubx
 */
public class FrameworkException extends BaseException {

	public FrameworkException(String message) {
		super(message);
	}

	public FrameworkException(String message, Throwable cause) {
		super(message, cause);
	}

	public FrameworkException(Throwable cause) {
		super(cause);
	}
}
