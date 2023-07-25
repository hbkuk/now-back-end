package com.now.core.comment.exception;

import com.now.common.exception.BadRequestException;
import com.now.common.exception.ErrorType;

public class CannotDeleteCommentException extends BadRequestException {
    public CannotDeleteCommentException(ErrorType errorType) {
        super(errorType);
    }
}
