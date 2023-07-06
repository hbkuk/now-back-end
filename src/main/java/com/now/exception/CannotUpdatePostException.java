package com.now.exception;

/**
 * 게시글을 수정할 수 없는 상황에서 던져지는 Unchecked Exception.
 */
public class CannotUpdatePostException extends RuntimeException {
    public CannotUpdatePostException(String message) {
        super(message);
    }
}
