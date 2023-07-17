package com.now.common.exception;

import lombok.Getter;

/**
 * 인증되지 않은 사용자가 보호된 리소스에 접근하려고 할 때 던져지는 예외
 */
@Getter
public class UnauthorizedException extends RuntimeException {

    private final int code;

    public UnauthorizedException(final ErrorType errorType) {
        super(errorType.getMessage());
        this.code = errorType.getCode();
    }
}
