package com.onclass.technology.domain.exception;

/**
 * Representa errores de validacion de formato o reglas de entrada.
 */
public class ValidationException extends BusinessException {

    public ValidationException(String message) {
        this(ErrorCode.VALIDATION_ERROR, message);
    }

    public ValidationException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
