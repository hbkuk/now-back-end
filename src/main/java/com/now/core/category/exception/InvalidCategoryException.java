package com.now.core.category.exception;

import com.now.common.exception.BadRequestException;
import com.now.common.exception.ErrorType;

/**
 * 잘못된 카테고리 전달한 상황에서 던져지는 Unchecked Exception.
 */
public class InvalidCategoryException extends BadRequestException {
    public InvalidCategoryException(ErrorType errorType) {
        super(errorType);
    }
}
