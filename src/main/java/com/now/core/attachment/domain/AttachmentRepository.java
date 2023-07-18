package com.now.core.attachment.domain;

import com.now.core.attachment.application.dto.ThumbNail;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 첨부파일 관련 정보를 관리하는 레포지토리
 */
@Repository
@RequiredArgsConstructor
public class AttachmentRepository {

    public final AttachmentMapper attachmentMapper;

    /**
     * 게시물 번호를 인자로 받아 해당하는 첨부파일 번호 목록을 반환
     *
     * @param postIdx 게시물 번호
     * @return 첨부파일 번호 목록
     */
    public List<Long> findAllIndexesByPostIdx(Long postIdx) {
        return attachmentMapper.findAllIndexesByPostIdx(postIdx);
    }

    /**
     * 첨부파일 번호를 인자로 받아 해당 첨부파일 객체를 반환
     *
     * @param attachmentIdx 첨부파일 번호
     * @return 첨부파일 객체
     */
    public Attachment findByAttachmentIdx(Long attachmentIdx) {
        return attachmentMapper.findByAttachmentIdx(attachmentIdx);
    }

    /**
     * 첨부파일 저장
     *
     * @param attachment 저장할 첨부파일 정보
     */
    public void saveAttachment(Attachment attachment) {
        attachmentMapper.saveAttachment(attachment);
    }

    /**
     * 대표 이미지 저장
     * 
     * @param attachment 저장할 대표 이미지 정보
     */
    public void saveThumbNail(Attachment attachment) {
        attachmentMapper.saveThumbNail(attachment);
    }

    /**
     * 첨부파일 번호에 해당하는 첨부파일 삭제
     *
     * @param attachmentIdx 첨부파일 번호
     */
    public void deleteAttachmentIdx(Long attachmentIdx) {
        attachmentMapper.deleteByAttachmentIdx(attachmentIdx);
    }

    /**
     * 게시물 번호에 해당하는 대표 이미지 정보를 반환
     *
     * @param postIdx 게시글 번호
     * @return 게시물 번호에 해당하는 대표 이미지 정보를 반환
     */
    public ThumbNail findThumbnailByPostIdx(Long postIdx) {
        return attachmentMapper.findThumbnailByPostIdx(postIdx);
    }

    /**
     * 대표 이미지 삭제
     * 
     * @param thumbNailIdx 대표 이미지 고유 식별자
     */
    public void deleteThumbNail(Long thumbNailIdx) {
        attachmentMapper.deleteThumbNail(thumbNailIdx);
    }

    /**
     * 대표 이미지 삭제
     * 
     * @param postIdx 게시글 번호
     */
    public void deleteThumbNailByPostIdx(Long postIdx) {
        attachmentMapper.deleteThumbNailByPostIdx(postIdx);
    }
}
