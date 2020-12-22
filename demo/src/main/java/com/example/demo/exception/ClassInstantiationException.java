package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.INTERNAL_SERVER_ERROR)
public class ClassInstantiationException extends RuntimeException {
	private static final long serialVersionUID = -2768664009191646015L;
	
	public ClassInstantiationException(Class<?> clazz, Throwable cause) {
		super("Unable create new instance of a class [" + clazz.getName() + "]", cause);
	}
}
