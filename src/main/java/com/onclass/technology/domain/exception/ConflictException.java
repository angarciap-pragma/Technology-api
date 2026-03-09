package com.onclass.technology.domain.exception;

public class ConflictException extends BusinessException {

    public ConflictException(String message) {
        this(ErrorCode.CONFLICT_ERROR, message);
    }

    public ConflictException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
