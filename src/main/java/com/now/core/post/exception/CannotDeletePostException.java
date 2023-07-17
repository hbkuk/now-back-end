package com.now.core.post.exception;

import com.now.common.exception.BadRequestException;
import com.now.common.exception.ErrorType;

/**
 * 게시글을 삭제할 수 없는 상황에서 던져지는 Unchecked Exception.
 */
public class CannotDeletePostException extends BadRequestException {
    public CannotDeletePostException(ErrorType errorType) {
        super(errorType);
    }
}
