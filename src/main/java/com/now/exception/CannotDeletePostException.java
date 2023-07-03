package com.now.exception;

public class CannotDeletePostException extends RuntimeException {

    public CannotDeletePostException(String message) {
        super(message);
    }
}
