package com.now.core.post.exception;

/**
 * 게시글을 삭제할 수 없는 상황에서 던져지는 Unchecked Exception.
 */
public class CannotDeletePostException extends RuntimeException {

    public CannotDeletePostException(String message) {
        super(message);
    }
}
