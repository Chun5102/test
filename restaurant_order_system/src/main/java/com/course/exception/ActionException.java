package com.course.exception;

public class ActionException extends Exception {

	private String errorCode;

	
	public ActionException(String message) {
		super(message);
	}
	
	public ActionException(String message, Throwable cause) {
		super(message, cause);
	}

	public ActionException(String errorCode,String message, Throwable cause) {
		super(message, cause);
		this.errorCode = errorCode;
	}

	public String getErrorCode() {
		return errorCode;
	}	
}
