package com.now.core.authentication.exception;

import com.now.common.exception.ErrorType;
import com.now.common.exception.UnauthorizedException;

/**
 * 인증에 실패한 경우 던져지는 Unchecked Exception.
 */
public class InvalidAuthenticationException extends UnauthorizedException {

    public InvalidAuthenticationException(final ErrorType errorType) {
        super(errorType);
    }
}
