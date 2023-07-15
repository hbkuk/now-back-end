package com.now.core.attachment.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * 첨부파일 관련 정보를 관리하는 레포지토리
 */
@Repository
public class AttachmentRepository {

    public final AttachmentMapper attachmentMapper;

    @Autowired
    public AttachmentRepository(AttachmentMapper attachmentMapper) {
        this.attachmentMapper = attachmentMapper;
    }
    
    /**
     * 첨부파일 저장
     *
     * @param attachment 저장할 첨부파일 정보
     */
    public void saveAttachment(Attachment attachment) {
        attachmentMapper.saveAttachment(attachment);
    }

    public void saveThumbNail(Attachment attachment) {
        attachmentMapper.saveThumbNail(attachment);
    }
}
