package com.now.core.attachment.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.now.core.attachment.domain.wrapped.AttachmentExtension;
import com.now.core.attachment.domain.wrapped.AttachmentSize;
import com.now.core.attachment.domain.wrapped.OriginalAttachmentName;
import lombok.*;

/**
 * 첨부파일을 나타내는 도메인 객체
 */
@Builder(toBuilder = true)
@ToString
@Getter
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class Attachment {
    /**
     * 첨부파일의 고유 식별자
     */
    private final Long attachmentIdx;

    /**
     * 서버 디렉토리에 저장된 첨부파일 이름(JSON 직렬화 시 숨김 처리)
     */
    @JsonIgnore
    private final String savedAttachmentName;

    /**
     * 사용자가 알고 있는 실제 첨부파일 이름
     */
    private final OriginalAttachmentName originalAttachmentName;

    /**
     * 첨부파일의 확장자명
     */
    private final AttachmentExtension attachmentExtension;

    /**
     * 첨부파일의 크기
     */
    private final AttachmentSize attachmentSize;

    /**
     *  게시글의 고유 식별자
     */
    private Long memberPostIdx;

    /**
     * 게시글 번호가 업데이트 된 해당 객체를 리턴
     *
     * @param memberPostIdx 게시글 번호
     */
    public Attachment updateMemberPostIdx(Long memberPostIdx) {
        this.memberPostIdx = memberPostIdx;
        return this;
    }
}
