package com.now.core.post.exception;

import com.now.common.exception.BadRequestException;
import com.now.common.exception.ErrorType;

/**
 * 게시글을 수정할 수 없는 상황에서 던져지는 Unchecked Exception.
 */
public class CannotUpdatePostException extends BadRequestException {
    public CannotUpdatePostException(ErrorType errorType) {
        super(errorType);
    }
}
