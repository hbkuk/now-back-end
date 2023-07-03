package com.now.exception;

public class CannotUpdatePostException extends RuntimeException {
    public CannotUpdatePostException(String message) {
        super(message);
    }
}
