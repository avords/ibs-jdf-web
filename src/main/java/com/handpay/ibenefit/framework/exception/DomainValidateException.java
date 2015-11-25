package com.handpay.ibenefit.framework.exception;


public class DomainValidateException extends BaseException {

	public DomainValidateException(String message) {
		super(message);
	}

	public DomainValidateException(String message, Throwable cause) {
		super(message, cause);
	}

	public DomainValidateException(Throwable cause) {
		super(cause);
	}

}
