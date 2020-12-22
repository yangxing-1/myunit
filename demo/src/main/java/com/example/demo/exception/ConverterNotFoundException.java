package com.example.demo.exception;

import org.springframework.core.convert.ConversionException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.INTERNAL_SERVER_ERROR)
public class ConverterNotFoundException extends ConversionException {
    private static final long serialVersionUID = 8978766151677873124L;

    public ConverterNotFoundException(Class<?> sourceType, Class<?> targetType) {
        super("No converter found capable of converting from type [" + sourceType.getName() + "] to type ["
                + targetType.getName() + "]");
    }
}
