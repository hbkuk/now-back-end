package com.now.core.attachment.domain;

import org.apache.ibatis.annotations.Mapper;

/**
 * 첨부파일 정보에 접근하는 매퍼 인터페이스
 */
@Mapper
public interface AttachmentMapper {

    /**
     * 첨부파일의 등록
     *
     * @param attachment 등록할 파일 정보
     */
    void saveAttachment(Attachment attachment);

    /**
     * 대표 이미지 등록
     *
     * @param attachment 등록할 대표 사진 게시글 정보
     */
    void saveThumbNail(Attachment attachment);
}
