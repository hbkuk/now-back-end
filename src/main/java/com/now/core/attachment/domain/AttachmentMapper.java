package com.now.core.attachment.domain;

import com.now.core.attachment.application.dto.ThumbNail;
import com.now.core.attachment.presentation.dto.AttachmentResponse;
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
    AttachmentResponse findAttachmentResponseByAttachmentIdx(Long attachmentIdx);

    /**
     * 첨부파일 번호를 인자로 받아 해당 첨부파일 객체를 반환
     *
     * @param attachmentIdx 첨부파일 번호
     * @return 첨부파일 객체
     */
    Attachment findAttachmentByAttachmentIdx(Long attachmentIdx);

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

    /**
     * 게시물 번호에 해당하는 대표 이미지 조회
     *
     * @param postIdx 게시글 번호
     * @return 게시물 번호에 해당하는 대표 이미지 조회
     */
    ThumbNail findThumbnailByPostIdx(Long postIdx);

    /**
     * 대표 이미지 삭제
     *
     * @param thumbNailIdx 대표 이미지 고유 식별자
     */
    void deleteThumbNail(Long thumbNailIdx);

    /**
     * 대표 이미지 삭제
     *
     * @param postIdx 게시글 번호
     */
    void deleteThumbNailByPostIdx(Long postIdx);


    /**
     * 해당 게시글 번호의 `attachment_idx` 컬럼을 null로 설정
     *
     * @param postIdx 게시글 번호
     */
    void clearThumbnail(Long postIdx);

    /**
     * 대표 이미지 정보 수정
     * 
     * @param attachment 수정할 대표 이미지 정보
     */
    void updateThumbnail(Attachment attachment);
}
