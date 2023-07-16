package com.now.core.attachment.domain;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 첨부파일 정보에 접근하는 매퍼 인터페이스
 */
@Mapper
public interface AttachmentMapper {

    /**
     * 게시물 번호를 인자로 받아 해당하는 첨부파일 번호 목록을 반환
     *
     * @param postIdx 게시물 번호
     * @return 첨부파일 번호 목록
     */
    List<Long> findAllIndexesByPostIdx(Long postIdx);

    /**
     * 첨부파일 번호를 인자로 받아 해당 첨부파일 객체를 반환
     *
     * @param attachmentIdx 첨부파일 번호
     * @return 첨부파일 객체
     */
    Attachment findByAttachmentIdx(Long attachmentIdx);

    /**
     * 첨부파일 저장
     *
     * @param attachment 저장할 첨부파일 정보
     */
    void saveAttachment(Attachment attachment);

    /**
     * 대표 이미지 저장
     *
     * @param attachment 저장할 대표 이미지 정보
     */
    void saveThumbNail(Attachment attachment);

    /**
     * 첨부파일 번호에 해당하는 첨부파일 삭제
     *
     * @param attachmentIdx 첨부파일 번호
     */
    void deleteByAttachmentIdx(Long attachmentIdx);

    /**
     * 게시글 번호에 해당하는 모든 첨부파일 삭제
     *
     * @param postIdx 게시글 번호
     */
    void deleteAllByPostIdx(Long postIdx);
}
