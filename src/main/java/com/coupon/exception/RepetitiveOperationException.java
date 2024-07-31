package com.coupon.exception;

public class RepetitiveOperationException extends RuntimeException {
    public RepetitiveOperationException() {
        super();
    }

    public RepetitiveOperationException(String message) {
        super(message);
    }
}
