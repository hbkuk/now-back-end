package com.now.core.attachment.application;

import com.now.common.exception.ErrorType;
import com.now.common.utils.AttachmentUtils;
import com.now.core.attachment.application.dto.ThumbNail;
import com.now.core.attachment.application.dto.UploadedAttachment;
import com.now.core.attachment.domain.Attachment;
import com.now.core.attachment.domain.AttachmentRepository;
import com.now.core.attachment.domain.constants.AttachmentType;
import com.now.core.attachment.domain.wrapped.AttachmentExtension;
import com.now.core.attachment.domain.wrapped.AttachmentSize;
import com.now.core.attachment.domain.wrapped.OriginalAttachmentName;
import com.now.core.attachment.exception.InvalidAttachmentException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 첨부파일 관련 비즈니스 로직을 처리하는 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AttachmentService {

    private final AttachmentRepository attachmentRepository;

    /**
     * 게시물 번호를 인자로 받아 해당하는 첨부파일 번호 목록을 반환
     *
     * @param postIdx 게시물 번호
     * @return 첨부파일 번호 목록
     */
    public List<Long> getAllIndexesByPostIdx(Long postIdx) {
        return attachmentRepository.findAllIndexesByPostIdx(postIdx);
    }

    /**
     * 첨부파일 번호를 인자로 받아 해당 첨부파일 객체를 반환
     *
     * @param attachmentIdx 첨부파일 번호
     * @return 첨부파일 객체
     */
    public Attachment getAttachment(Long attachmentIdx) {
        Attachment attachment = attachmentRepository.findByAttachmentIdx(attachmentIdx);
        if(attachment == null) {
            throw new InvalidAttachmentException(ErrorType.NOT_FOUND_ATTACHMENT);
        }

        return attachment;
    }

    /**
     * 첨부파일 저장
     *
     * @param attachment 저장할 첨부파일 정보
     */
    public void saveAttachment(Attachment attachment) {
        attachmentRepository.saveAttachment(attachment);
    }

    /**
     * 첨부파일을 서버 디렉토리에 업로드 후 데이터베이스에 첨부파일 저장
     *
     * @param multipartFiles MultipartFile[] 객체
     * @param postIdx        게시글 번호
     * @param attachmentType 첨부파일 업로드 타입
     */
    public void saveAttachments(MultipartFile[] multipartFiles, Long postIdx, AttachmentType attachmentType) {
        if(!hasExistUploadFile(multipartFiles)) {
            return;
        }
        List<Attachment> attachments = uploadedAttachment(multipartFiles, attachmentType);
        attachments.forEach(attachment -> saveAttachment(attachment.updateMemberPostIdx(postIdx)));
    }

    /**
     * 첨부파일을 서버 디렉토리에 업로드 후 데이터베이스에 첨부파일 저장, 대표 이미지 저장
     *
     * @param multipartFiles multipartAttachment[] 객체
     * @param postIdx        게시글 번호
     * @param attachmentType 첨부파일 업로드 타입
     */
    public void saveAttachmentsWithThumbnail(MultipartFile[] multipartFiles, Long postIdx, AttachmentType attachmentType) {
        if(!hasExistUploadFile(multipartFiles)) {
            return;
        }
        List<Attachment> attachments = uploadedAttachment(multipartFiles, attachmentType);
        attachments.forEach(attachment -> saveAttachment(attachment.updateMemberPostIdx(postIdx)));
        attachmentRepository.saveThumbNail(attachments.get(0));
    }

    /**
     * 첨부파일을 업로드 하고, 업로드된 첨부파일 목록을 반환
     *
     * @param multipartFiles 업로드할 {@link MultipartFile} 배열
     * @param uploadType     첨부파일 업로드 타입을 정의한 {@link AttachmentType} 객체
     * @return 업로드된 첨부파일 목록
     */
    public List<Attachment> uploadedAttachment(MultipartFile[] multipartFiles, AttachmentType uploadType) {
        return Arrays.stream(multipartFiles)
                .limit(uploadType.getMaxUploadCount())
                .map(multipartFile -> convertToAttachment(AttachmentUtils.processServerUploadFile(multipartFile), uploadType))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * 새로운 첨부파일들과 이전에 업로드된 첨부파일 인덱스 목록을 기준으로 첨부파일을 저장 또는 삭제
     *
     * @param multipartFiles            업로드할 {@link MultipartFile} 배열
     * @param previouslyUploadedIndexes 이전에 업로드된 첨부파일 번호 목록
     * @param postIdx                   게시글 번호
     * @param attachmentType            첨부파일 업로드 타입을 정의한 {@link AttachmentType} 객체
     */
    public void updateAttachments(MultipartFile[] multipartFiles, List<Long> previouslyUploadedIndexes,
                                  Long postIdx, AttachmentType attachmentType) {
        List<Long> indexesToDelete = getAllIndexesByPostIdx(postIdx);
        if(indexesToDelete.isEmpty()) {
            saveAttachments(multipartFiles, postIdx, attachmentType);
            return;
        }

        indexesToDelete.removeAll(previouslyUploadedIndexes);
        deleteByAttachmentIndexes(indexesToDelete);
        saveAttachments(multipartFiles, postIdx, attachmentType);
    }

    /**
     * 대표 이미지 삭제
     *
     * @param thumbNailIdx 삭제할 대표 이미지 고유 식별자
     */
    private void deleteThumbNail(Long thumbNailIdx) {
        attachmentRepository.deleteThumbNail(thumbNailIdx);
    }

    /**
     * 게시물 번호에 해당하는 대표 이미지 정보를 반환
     *
     * @param postIdx 게시글 번호
     * @return 게시물 번호에 해당하는 대표 이미지 정보를 반환
     */
    public ThumbNail getThumbnailByPostIdx(Long postIdx) {
        return attachmentRepository.findThumbnailByPostIdx(postIdx);
    }

    /**
     * 새로운 첨부파일들과 이전에 업로드된 첨부파일 인덱스 목록을 기준으로 첨부파일을 저장 또는 삭제
     *
     * @param multipartFiles            업로드할 {@link MultipartFile} 배열
     * @param previouslyUploadedIndexes 이전에 업로드된 첨부파일 번호 목록
     * @param postIdx                   게시글 번호
     * @param attachmentType            첨부파일 업로드 타입을 정의한 {@link AttachmentType} 객체
     */
    public void updateAttachmentsWithThumbnail(MultipartFile[] multipartFiles, List<Long> previouslyUploadedIndexes, Long postIdx, AttachmentType attachmentType) {
        List<Long> indexesToDelete = getAllIndexesByPostIdx(postIdx);
        if(indexesToDelete.isEmpty()) {
            saveAttachments(multipartFiles, postIdx, attachmentType);
            return;
        }

        indexesToDelete.removeAll(previouslyUploadedIndexes);

        ThumbNail thumbNail = getThumbnailByPostIdx(postIdx);

        if (indexesToDelete.contains(thumbNail.getAttachmentIdx())) {
            deleteThumbNail(thumbNail.getAttachmentIdx());
            deleteByAttachmentIndexes(indexesToDelete);
            saveAttachments(multipartFiles, postIdx, attachmentType);
        }

        if(!indexesToDelete.contains(thumbNail.getAttachmentIdx())) {
            deleteByAttachmentIndexes(indexesToDelete);
            saveAttachments(multipartFiles, postIdx, attachmentType);
        }
    }

    /**
     * 첨부파일 번호 목록에 해당하는 첨부파일들을 삭제
     *
     * @param attachmentIndexes 첨부파일 번호 목록
     */
    public void deleteByAttachmentIndexes(List<Long> attachmentIndexes) {
        attachmentIndexes.forEach(this::deleteAttachment);
    }

    /**
     * 첨부파일 번호에 해당하는 첨부파일 삭제
     *
     * @param attachmentIdx 첨부파일 번호
     */
    public void deleteAttachment(Long attachmentIdx) {
        AttachmentUtils.deleteUploadedFile(getAttachment(attachmentIdx).getSavedAttachmentName());
        attachmentRepository.deleteAttachmentIdx(attachmentIdx);
    }

    /**
     * 게시글 번호에 해당하는 모든 첨부파일 삭제
     *
     * @param postIdx 게시글 번호
     */
    public void deleteAllByPostIdx(Long postIdx) {
        attachmentRepository.findAllIndexesByPostIdx(postIdx)
                                .forEach(this::deleteAttachment);
    }

    /**
     * 게시글 번호에 해당하는 모든 첨부파일 삭제 및 대표 이미지 삭제
     *
     * @param postIdx 게시글 번호
     */
    public void deleteAllByPostIdxWithThumbNail(Long postIdx) {
        attachmentRepository.deleteThumbNailByPostIdx(postIdx);
        attachmentRepository.findAllIndexesByPostIdx(postIdx)
                .forEach(this::deleteAttachment);
    }

    /**
     * {@link UploadedAttachment} 객체를 {@link Attachment} 객체로 변환
     *
     * @param uploadedAttachment 업로드된 첨부파일 정보를 담고 있는 {@link UploadedAttachment} 객체
     * @param uploadType         첨부파일 업로드 타입을 정의한 {@link AttachmentType} 객체
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
        } catch (InvalidAttachmentException e) {
            log.error(e.getMessage(), e);
            AttachmentUtils.deleteUploadedFile(uploadedAttachment.getSystemName());
            return null;
        }
    }

    /**
     * 업로드된 파일이 있는지 확인
     *
     * @param multipartFiles 업로드된 파일 배열
     * @return 업로드된 파일 여부
     */
    private static boolean hasExistUploadFile(MultipartFile[] multipartFiles) {
        return multipartFiles != null && multipartFiles.length > 0;
    }
}

