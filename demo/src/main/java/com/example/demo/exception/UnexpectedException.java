package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.INTERNAL_SERVER_ERROR)
public class UnexpectedException extends RuntimeException {
	private static final long serialVersionUID = 6036721898128842632L;

	public UnexpectedException(String msg) {
		super(msg);
	}
	
	public UnexpectedException(Throwable t) {
		super(t);
	}
}
