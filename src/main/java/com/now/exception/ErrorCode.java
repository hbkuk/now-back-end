package com.now.exception;

import org.springframework.context.support.MessageSourceAccessor;

/**
 * API 예외 코드를 정의한 enum
 */
public enum ErrorCode {
    INVALID_PARAM("PARAM-001"),
    INVALID_REQUEST("REQUEST-001"),
    INVALID_DATA("REQUEST-002"),
    DUPLICATE_USER("USER-001"),
    AUTHENTICATION_FAILED("AUTH-001"),
    INVALID_TOKEN("AUTH-002"),
    EXPIRED_TOKEN("AUTH-003"),
    PERMISSION_DENIED("AUTH-004"),
    SERVER_INTERNAL_ERROR("SERVER-001");

    private final String code;

    /**
     * ErrorCode 생성자
     *
     * @param code    예외 코드
     */
    ErrorCode(String code) {
        this.code = code;
    }

    /**
     * 예외 코드를 반환
     *
     * @return 예외 코드
     */
    public String getCode() {
        return code;
    }
}
