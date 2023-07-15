package com.now.core.attachment.domain.wrapped;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 원본 첨부파일의 이름을 나타내는 원시값 포장 객체
 */
@ToString
@Getter
@NoArgsConstructor(force = true)
public class OriginalAttachmentName {
    private static final int MAX_VALUE_LENGTH = 500;
    private final String originalAttachmentName;

    /**
     * originalAttachmentName 객체 생성
     *
     * @param fileName 첨부파일의 이름
     * @throws IllegalArgumentException 첨부파일의 이름이 정해진 길이를 초과할 경우 예외를 발생시킴
     */
    public OriginalAttachmentName(String fileName) {
        if (fileName.length() > MAX_VALUE_LENGTH) {
            throw new IllegalArgumentException("첨부파일 이름은 500자를 초과할 수 없습니다.");
        }
        this.originalAttachmentName = fileName;
    }
}
