package com.now.core.member.exception;

import com.now.common.exception.BadRequestException;
import com.now.common.exception.ErrorType;

public class InvalidMemberException extends BadRequestException {

    public InvalidMemberException(final ErrorType errorType) {
        super(errorType);
    }
}
