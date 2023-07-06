package com.now.exception;

import java.util.List;

/**
 * 중복되는 유저 정보가 하나라도 존재한다면 던져지는 Unchecked Exception.
 */
public class DuplicateUserException extends RuntimeException {

    private List<String> messages;

    public DuplicateUserException(List<String> messages) {
        this.messages = messages;
    }

    public List<String> getMessages() {
        return messages;
    }
}
