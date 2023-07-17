package com.now.common.exception;

import lombok.Getter;

/**
 * 클라이언트의 잘못된 요청이 발생했을 때 던져지는 예외
 */
@Getter
public class BadRequestException extends RuntimeException {

    private final int code;

    public BadRequestException(final ErrorType errorType) {
        super(errorType.getMessage());
        this.code = errorType.getCode();
    }
}
