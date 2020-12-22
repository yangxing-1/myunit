package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.INTERNAL_SERVER_ERROR)
public class CloneException extends RuntimeException {
	private static final long serialVersionUID = -74975982546209140L;
	
	public CloneException(Object target, Throwable cause) {
		super("Unable clone object [" + target + "].", cause);
	}
}
