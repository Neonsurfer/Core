package com.simple.core.error;

public abstract class BusinessException extends RuntimeException {
    private final int errorCode;

    protected BusinessException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
