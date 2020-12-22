package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.INTERNAL_SERVER_ERROR)
public class EmptyArrayException extends RuntimeException {
	private static final long serialVersionUID = -98265843884386419L;
}
