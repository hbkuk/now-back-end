package com.now.exception;

/**
 * 비밀글로 설정된 문의글을 볼 수 없는 상황에서 던져지는 Unchecked Exception.
 */
public class CannotViewInquiryException extends RuntimeException {
    public CannotViewInquiryException(String message) {
        super(message);
    }
}
