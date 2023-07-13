package com.now.core.authentication.exception;

/**
 * 인증에 실패한 경우 던져지는 Unchecked Exception.
 */
public class AuthenticationFailedException extends RuntimeException {
    public AuthenticationFailedException(String message) {
        super(message);
    }
}
