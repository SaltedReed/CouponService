package com.coupon.exception;

public class RateLimitException extends RuntimeException{
    public RateLimitException() {
        super();
    }

    public RateLimitException(String message) {
        super(message);
    }
}
