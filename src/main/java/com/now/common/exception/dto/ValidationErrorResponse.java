package com.now.common.exception.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 유효성 검증 시 실패한 에러를 응답하는 객체
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class ValidationErrorResponse {

    private int errorCode;
    private Map<String, String> message;

    public ValidationErrorResponse(final int errorCode, final Map<String, String> message) {
        this.errorCode = errorCode;
        this.message = message;
    }
}
