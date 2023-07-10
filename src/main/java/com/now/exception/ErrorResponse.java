package com.now.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;

/**
 * API 예외 응답을 나타내는 객체
 */
@Getter
@Setter
public class ErrorResponse {

    private String errorCode;
    private String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String detail;

    /**
     * ErrorResponse 생성자
     *
     * @param errorCode     예외 코드
     * @param messageSource MessageSource 객체
     */
    public ErrorResponse(ErrorCode errorCode, MessageSourceAccessor messageSource) {
        this.errorCode = errorCode.getCode();
        this.message = messageSource.getMessage("error.code." + errorCode.getCode());
    }
}
