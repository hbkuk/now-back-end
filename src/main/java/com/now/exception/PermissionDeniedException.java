package com.now.exception;

/**
 * 권한이 없는 상황에서 던져지는 Unchecked Exception.
 */
public class PermissionDeniedException extends RuntimeException {
    /**
     * 주어진 메시지로 권한 거부 예외를 생성합니다.
     *
     * @param message 예외 메시지
     */
    public PermissionDeniedException(String message) {
        super(message);
    }
}