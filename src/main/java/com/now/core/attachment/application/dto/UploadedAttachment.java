package com.now.core.attachment.application.dto;

import lombok.*;

/**
 * 서버 디렉토리에 업로드된 첨부파일 정보를 담고있는 객체
 */
@Builder(toBuilder = true)
@ToString
@Getter
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class UploadedAttachment {

    private final String systemName;
    private final String originalAttachmentName;
    private final String extension;
    private final int attachmentSize;
}
