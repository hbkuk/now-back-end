package com.now.core.authentication.exception;

import com.now.common.exception.ErrorType;
import com.now.common.exception.UnauthorizedException;

public class InvalidTokenException extends UnauthorizedException {

    public InvalidTokenException(final ErrorType errorType) {
        super(errorType);
    }
}
