package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.INTERNAL_SERVER_ERROR)
public class EncryptException extends RuntimeException {
    private static final long serialVersionUID = 3774668281882492400L;

    public EncryptException(String message) {
        super(message);
    }
    
    public EncryptException(Throwable t) {
        super(t);
    }
}
