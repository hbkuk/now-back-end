package com.now.core.post.exception;

/**
 * 게시글을 작성할 수 없는 상황에서 던져지는 Unchecked Exception.
 */
public class CannotWritePostException extends RuntimeException {
    public CannotWritePostException(String message) {
        super(message);
    }
}
