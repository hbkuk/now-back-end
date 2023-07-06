package com.now.exception;

import java.util.List;

public class DuplicateUserException extends RuntimeException {

    private List<String> messages;

    public DuplicateUserException(List<String> messages) {
        this.messages = messages;
    }

    public List<String> getMessages() {
        return messages;
    }
}
