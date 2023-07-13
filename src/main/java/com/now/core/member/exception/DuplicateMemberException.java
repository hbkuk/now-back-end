package com.now.core.member.exception;

import java.util.List;

/**
 * 중복되는 회원 정보가 하나라도 존재한다면 던져지는 Unchecked Exception.
 */
public class DuplicateMemberException extends RuntimeException {

    private List<String> messages;

    public DuplicateMemberException(List<String> messages) {
        this.messages = messages;
    }

    public List<String> getMessages() {
        return messages;
    }
}
