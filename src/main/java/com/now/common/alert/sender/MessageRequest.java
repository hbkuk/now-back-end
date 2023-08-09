package com.now.common.alert.sender;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 메시지 요청을 나타내는 객체
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MessageRequest {

    private String text;

    public MessageRequest(final String text) {
        this.text = text;
    }
}
