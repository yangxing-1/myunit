package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.INTERNAL_SERVER_ERROR)
public class SpringException extends RuntimeException {
	private static final long serialVersionUID = 3199336072993488942L;
	
	public SpringException(String message) {
		super(message);
	}
	
	public SpringException(Throwable t) {
		super(t);
	}
}
