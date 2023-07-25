package com.now.core.comment.exception;

import com.now.common.exception.BadRequestException;
import com.now.common.exception.ErrorType;

public class CannotUpdateCommentException extends BadRequestException  {
    public CannotUpdateCommentException(ErrorType errorType) {
        super(errorType);
    }
}
