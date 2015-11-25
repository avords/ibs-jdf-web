package com.handpay.ibenefit.framework.exception;

import com.handpay.ibenefit.framework.util.MessageUtils;



public class BaseException extends Exception {

    private static final long serialVersionUID = 5168260832131670286L;
	
    private String errorCode;
    
	public BaseException (String errorCode,String message,Throwable throwable){
		super(message,throwable);
		this.errorCode = errorCode;
	}
	
	public BaseException (String errorCode,Throwable throwable){
		super(throwable);
		this.errorCode = errorCode;
	}
	
	public BaseException (String errorCode){
		super();
		this.errorCode = errorCode;
	}
	
	public BaseException (String errorCode,String message){
		super(message);
		this.errorCode = errorCode;
	}
	
	public BaseException(String message, String[] values) {
        super(MessageUtils.getMessage(message,values));
    }
	
	public BaseException(Throwable cause) {
		super(cause);
	}
	
	/**
	 * @return the errorCode
	 */
	public String getErrorCode() {
		return errorCode;
	}
	
}
