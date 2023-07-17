package com.now.core.attachment.domain.wrapped;

import com.now.common.exception.ErrorType;
import com.now.core.attachment.exception.InvalidAttachmentException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 첨부파일의 크기를 나타내는 원시값 포장 객체
 */
@ToString
@Getter
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class AttachmentSize {
    private final int attachmentSize;

    /**
     * AttachmentSize 객체를 생성
     *
     * @param attachmentSize       첨부파일의 크기 값
     * @param maxAttachmentSize  허용되는 최대 첨부파일의 크기
     * @throws InvalidAttachmentException 첨부파일의 크기가 허용된 크기를 초과할 경우 발생하는 예외
     */
    public AttachmentSize(int attachmentSize, int maxAttachmentSize) {
        if (attachmentSize > maxAttachmentSize) {
            throw new InvalidAttachmentException(ErrorType.INVALID_ATTACHMENT_SIZE);
        }
        this.attachmentSize = attachmentSize;
    }
}
