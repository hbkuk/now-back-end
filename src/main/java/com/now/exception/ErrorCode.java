package com.now.exception;

/**
 * API 예외 코드를 정의한 enum
 */
public enum ErrorCode {
    INVALID_PARAM("PARAM-001", "형식에 맞지 않는 파라미터 전달"),

    INVALID_REQUEST("REQUEST-001", "잘못된 요청"),
    INVALID_DATA("REQUEST-002", "형식에 맞지 않는 데이터 전달"),

    DUPLICATE_USER("USER-001", "중복된 유저 정보"),

    AUTHENTICATION_FAILED("AUTH-001", "인증 실패"),

    SERVER_INTERNAL_ERROR("SERVER-001", "서버 내부 오류");

    private final String code;
    private final String message;

    /**
     * ErrorCode 생성자
     *
     * @param code    예외 코드
     * @param message 예외 메시지
     */
    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 예외 코드를 반환
     *
     * @return 예외 코드
     */
    public String getCode() {
        return code;
    }

    /**
     * 예외 메시지를 반환
     *
     * @return 예외 메시지
     */
    public String getMessage() {
        return message;
    }
}
