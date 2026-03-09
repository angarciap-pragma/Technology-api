package com.onclass.technology.domain.exception;

public class NotFoundException extends BusinessException {

    public NotFoundException(String message) {
        this(ErrorCode.NOT_FOUND_ERROR, message);
    }

    public NotFoundException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
