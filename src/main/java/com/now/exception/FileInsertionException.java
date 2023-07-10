package com.now.exception;

/**
 * 파일 삽입 중에 서버 측 문제로 인해 예외가 발생했을 때 던져지는 Unchecked Exception.
 */
public class FileInsertionException extends RuntimeException {
    public FileInsertionException(String message) {
        super(message);
    }
}
