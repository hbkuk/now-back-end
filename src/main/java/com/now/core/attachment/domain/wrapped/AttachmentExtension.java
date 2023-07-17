package com.now.core.attachment.domain.wrapped;

import com.now.common.exception.ErrorType;
import com.now.core.attachment.exception.InvalidAttachmentException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

/**
 * 첨부파일의 확장자를 나타내는 원시값 포장 객체
 */
@ToString
@Getter
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class AttachmentExtension {
    private final String attachmentExtension;

    /**
     * Extension 객체 생성
     *
     * @param extension     첨부파일의 확장자
     * @param allowedExtensions 허용되는 확장자 그룹
     * @throws InvalidAttachmentException 허용되지 않는 확장자일 경우 예외를 발생시킴
     */
    public AttachmentExtension(String extension, List<String> allowedExtensions) {

        if(!allowedExtensions.contains(extension)) {
            throw new InvalidAttachmentException(ErrorType.INVALID_ATTACHMENT_EXTENSION);
        }
        this.attachmentExtension = extension;
    }
}
