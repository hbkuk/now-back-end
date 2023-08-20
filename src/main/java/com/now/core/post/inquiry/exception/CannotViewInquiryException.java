package com.now.core.post.inquiry.exception;

import com.now.common.exception.BadRequestException;
import com.now.common.exception.ErrorType;

/**
 * 비밀글로 설정된 문의글을 볼 수 없는 상황에서 던져지는 Unchecked Exception.
 */
public class CannotViewInquiryException extends BadRequestException {
    public CannotViewInquiryException(ErrorType errorType) {
        super(errorType);
    }
}
