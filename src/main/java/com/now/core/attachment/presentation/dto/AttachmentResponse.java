package com.now.core.attachment.presentation.dto;

import lombok.*;

@Builder(toBuilder = true)
@Getter
@ToString(callSuper = true)
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class AttachmentResponse {

    /**
     * 첨부파일의 고유 식별자
     */
    private final Long attachmentIdx;


    /**
     * 사용자가 알고 있는 실제 첨부파일 이름
     */
    private final String originalAttachmentName;

    /**
     * 서버 디렉토리에 저장된 첨부파일 이름
     */
    private final String savedAttachmentName;

    /**
     * 첨부파일의 확장자명
     */
    private final String attachmentExtension;

    /**
     * 첨부파일의 크기
     */
    private final Integer attachmentSize;

    /**
     *  게시글의 고유 식별자
     */
    private Long postIdx;
}
