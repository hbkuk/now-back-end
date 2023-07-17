package com.now.core.post.exception;

import com.now.common.exception.BadRequestException;
import com.now.common.exception.ErrorType;

/**
 * 게시글을 찾을 수 없는 상황에서 던져지는 Unchecked Exception.
 */
public class InvalidPostException extends BadRequestException {
    public InvalidPostException(ErrorType errorType) {
        super(errorType);
    }
}
