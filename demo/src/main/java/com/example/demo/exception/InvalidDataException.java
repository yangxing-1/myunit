package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.INTERNAL_SERVER_ERROR)
public class InvalidDataException extends RuntimeException {
	private static final long serialVersionUID = -1478017360360904746L;
	
	public InvalidDataException(String message) {
		super(message);
	}
}
