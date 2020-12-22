package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.INTERNAL_SERVER_ERROR)
public class ZipException extends RuntimeException {
    private static final long serialVersionUID = 6698706736425765959L;
    
    public ZipException(String msg) {
        super(msg);
    }
    
    public ZipException(Throwable throwable) {
        super(throwable);
    }
}
