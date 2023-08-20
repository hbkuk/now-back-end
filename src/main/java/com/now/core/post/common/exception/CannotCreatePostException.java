package com.now.core.post.common.exception;

import com.now.common.exception.BadRequestException;
import com.now.common.exception.ErrorType;

/**
 * 게시글을 작성할 수 없는 상황에서 던져지는 Unchecked Exception.
 */
public class CannotCreatePostException extends BadRequestException {
    public CannotCreatePostException(ErrorType errorType) {
        super(errorType);
    }
}
