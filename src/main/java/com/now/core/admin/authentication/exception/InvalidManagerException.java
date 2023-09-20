package com.now.core.admin.authentication.exception;

import com.now.common.exception.BadRequestException;
import com.now.common.exception.ErrorType;

public class InvalidManagerException extends BadRequestException {

    public InvalidManagerException(final ErrorType errorType) {
        super(errorType);
    }
}
