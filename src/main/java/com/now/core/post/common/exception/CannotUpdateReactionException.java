package com.now.core.post.common.exception;

import com.now.common.exception.BadRequestException;
import com.now.common.exception.ErrorType;

/**
 * 게시글의 반응을 업데이트할 수 없는 상황에서 던져지는 Unchecked Exception.
 */
public class CannotUpdateReactionException extends BadRequestException {
    public CannotUpdateReactionException(ErrorType errorType) {
        super(errorType);
    }
}
