package com.now.common.exception;

import lombok.Getter;

/**
 * 인가되지 않은 사용자가 보호된 리소스에 접근하려고 할 때 던져지는 예외
 */
@Getter
public class ForbiddenException extends RuntimeException {

    private final int code;

    public ForbiddenException(final ErrorType errorType) {
        super(errorType.getMessage());
        this.code = errorType.getCode();
    }
}
