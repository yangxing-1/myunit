package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.INTERNAL_SERVER_ERROR)
public class TypeMismatchException extends RuntimeException {
	private static final long serialVersionUID = 9193226953241045578L;
	
	public TypeMismatchException(Class<?> expectedType, Class<?> actualType) {
		super("Expected type [" + expectedType.getName() + "], but actual type [" + actualType.getName() + "].");
	}
	
	public TypeMismatchException(String expectedTypeName, Class<?> actualType) {
		super("Expected type [" + expectedTypeName + "], but actual type [" + actualType.getName() + "].");
	}
}
