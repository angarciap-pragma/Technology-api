package com.onclass.technology.domain.exception;

/**
 * Representa errores cuando un recurso solicitado no existe.
 */
public class NotFoundException extends BusinessException {

    public NotFoundException(String message) {
        this(ErrorCode.NOT_FOUND_ERROR, message);
    }

    public NotFoundException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
