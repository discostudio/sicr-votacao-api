package org.fhc.sicrvotacaoapi.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Getter
public class BusinessException extends RuntimeException {

    private final HttpStatus status;
    private final Map<String, String> fieldErrors;

    public BusinessException(String message, HttpStatus status, Map<String, String> fieldErrors) {
        super(message);
        this.status = status;
        this.fieldErrors = fieldErrors;
    }
}
