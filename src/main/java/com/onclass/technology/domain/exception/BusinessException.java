package com.onclass.technology.domain.exception;

import lombok.Getter;

/**
 * Excepcion base para representar errores controlados del dominio.
 */
@Getter
public abstract class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;

    protected BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

}
