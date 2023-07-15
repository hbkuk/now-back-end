package com.now.core.attachment.application;

import com.now.common.utils.AttachmentUtils;
import com.now.core.attachment.application.dto.UploadedAttachment;
import com.now.core.attachment.domain.Attachment;
import com.now.core.attachment.domain.AttachmentRepository;
import com.now.core.attachment.domain.constants.AttachmentType;
import com.now.core.attachment.domain.wrapped.AttachmentExtension;
import com.now.core.attachment.domain.wrapped.AttachmentSize;
import com.now.core.attachment.domain.wrapped.OriginalAttachmentName;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 파일 관련 비즈니스 로직을 처리하는 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AttachmentService {

    private final AttachmentRepository attachmentRepository;

    /**
     * 데이터베이스에 파일 저장
     *
     * @param attachment 파일 정보가 담긴 객체
     */
    public void saveAttachments(Attachment attachment) {
        attachmentRepository.saveAttachment(attachment);
    }

    /**
     * 파일을 서버 디렉토리에 업로드 후 데이터베이스에 파일 저장
     *
     * @param multipartFiles MultipartFile[] 객체
     * @param postIdx        게시글 번호
     * @param attachmentType 파일 업로드 타입
     */
    public void saveAttachments(MultipartFile[] multipartFiles, Long postIdx, AttachmentType attachmentType) {
        List<Attachment> attachments = uploadedAttachment(multipartFiles, attachmentType);
        attachments.forEach(attachment -> saveAttachments(attachment.updateMemberPostIdx(postIdx)));
    }

    /**
     * 파일을 서버 디렉토리에 업로드 후 데이터베이스에 파일 저장, 대표이미지 저장
     *
     * @param multipartFiles multipartAttachment[] 객체
     * @param postIdx        게시글 번호
     * @param attachmentType 파일 업로드 타입
     */
    public void saveAttachmentsWithThumbnail(MultipartFile[] multipartFiles, Long postIdx, AttachmentType attachmentType) {
        List<Attachment> attachments = uploadedAttachment(multipartFiles, attachmentType);
        attachments.forEach(attachment -> saveAttachments(attachment.updateMemberPostIdx(postIdx)));
        attachmentRepository.saveThumbNail(attachments.get(0));
    }

    /**
     * 파일을 업로드 하고, 업로드된 파일 목록을 반환
     *
     * @param multipartFiles 업로드할 {@link MultipartFile} 배열
     * @param uploadType     파일 업로드 타입을 정의한 {@link AttachmentType} 객체
     * @return 업로드된 파일 목록
     */
    public List<Attachment> uploadedAttachment(MultipartFile[] multipartFiles, AttachmentType uploadType) {
        return Arrays.stream(multipartFiles)
                .limit(uploadType.getMaxUploadCount())
                .map(multipartFile -> convertToAttachment(AttachmentUtils.processServerUploadFile(multipartFile), uploadType))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * {@link UploadedAttachment} 객체를 {@link Attachment} 객체로 변환
     *
     * @param uploadedAttachment 업로드된 파일 정보를 담고 있는 {@link UploadedAttachment} 객체
     * @param uploadType         파일 업로드 타입을 정의한 {@link AttachmentType} 객체
     * @return 변환된 {@link Attachment} 객체
     */
    private Attachment convertToAttachment(UploadedAttachment uploadedAttachment, AttachmentType uploadType) {
        if (uploadedAttachment == null) {
            return null;
        }

        try {
            return Attachment.builder()
                    .savedAttachmentName(uploadedAttachment.getSystemName())
                    .originalAttachmentName(new OriginalAttachmentName(uploadedAttachment.getOriginalAttachmentName()))
                    .attachmentExtension(new AttachmentExtension(AttachmentUtils.extractFileExtension(uploadedAttachment.getSystemName()), uploadType.getAllowedExtensions()))
                    .attachmentSize(new AttachmentSize(uploadedAttachment.getAttachmentSize(), uploadType.getMaxUploadSize()))
                    .build();
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage(), e);
            AttachmentUtils.deleteUploadedFile(uploadedAttachment.getSystemName());
            return null;
        }
    }
}

