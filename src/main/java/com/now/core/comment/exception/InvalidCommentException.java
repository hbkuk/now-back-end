package com.now.core.comment.exception;

import com.now.common.exception.BadRequestException;
import com.now.common.exception.ErrorType;

public class InvalidCommentException extends BadRequestException {
    public InvalidCommentException(ErrorType errorType) {
        super(errorType);
    }
}
