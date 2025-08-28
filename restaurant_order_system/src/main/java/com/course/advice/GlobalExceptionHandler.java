package com.course.advice;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.course.model.ApiResponse;

import ch.qos.logback.core.joran.spi.ActionException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
	
	@ExceptionHandler(value = ActionException.class)
	public ApiResponse<Map<String,String>> actionExceptionHandler(ActionException ae)
	{
		logger.error("Action error", ae);
		ApiResponse<Map<String,String>> response=ApiResponse.error("999", "系統忙碌中，請稍後再試");
		return response;
	}
	
	@ExceptionHandler(value = Exception.class)
	public ApiResponse<Map<String, String>> allExceptionHandler(Exception e) 
	{
        ApiResponse<Map<String, String>> response = ApiResponse.error("505", "系統忙碌中");

        return response;
	}
}
