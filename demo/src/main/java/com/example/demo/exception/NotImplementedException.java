package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class NotImplementedException extends RuntimeException {
    private static final long serialVersionUID = 4274117768617586829L;

    public NotImplementedException() {
        super();
    }

    public NotImplementedException(String msg) {
        super(msg);
    }

    public NotImplementedException(Throwable t) {
        super(t);
    }
}
