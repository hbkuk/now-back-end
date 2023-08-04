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
import com.now.core.attachment.exception.CannotUpdateThumbnailException;
import com.now.core.attachment.exception.InvalidAttachmentException;
import com.now.core.attachment.presentation.dto.AttachmentResponse;
import com.now.core.post.application.dto.AddNewAttachments;
import com.now.core.post.application.dto.UpdateExistingAttachments;
import com.now.core.post.domain.constants.UpdateOption;
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
     * 업로드된 파일이 있는지 확인
     *
     * @param multipartFiles 업로드된 파일 배열
     * @return 업로드된 파일 여부
     */
    private static boolean hasExistUploadFile(MultipartFile[] multipartFiles) {
        return multipartFiles != null && multipartFiles.length > 0;
    }

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
    public AttachmentResponse getAttachment(Long attachmentIdx) {
        AttachmentResponse attachment = attachmentRepository.findAttachmentResponseByAttachmentIdx(attachmentIdx);
        if (attachment == null) {
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
        if (!hasExistUploadFile(multipartFiles)) {
            return;
        }
        List<Attachment> attachments = uploadedAttachments(multipartFiles, attachmentType);
        attachments.forEach(attachment -> saveAttachment(attachment.updateMemberPostIdx(postIdx)));
    }

    /**
     * 대표 이미지를 업데이트
     *
     * @param multipartFile 업데이트할 대표 이미지 파일
     * @param postIdx       게시글 번호
     * @param attachmentType 첨부파일 업로드 타입을 정의한 {@link AttachmentType} 객체
     */
    public void updateThumbnail(MultipartFile multipartFile, Long postIdx, AttachmentType attachmentType) {
        if (multipartFile.isEmpty()) {
            return;
        }
        Attachment thumbNailAttachment = uploadedAttachment(multipartFile, attachmentType);
        saveAttachment(thumbNailAttachment.updateMemberPostIdx(postIdx));
        attachmentRepository.updateThumbnail(thumbNailAttachment);
    }

    /**
     * 첨부파일을 서버 디렉토리에 업로드 후 데이터베이스에 첨부파일 저장, 대표 이미지 저장
     *
     * @param multipartFiles multipartAttachment[] 객체
     * @param postIdx        게시글 번호
     * @param attachmentType 첨부파일 업로드 타입
     */
    public void saveAttachmentsWithThumbnail(MultipartFile[] multipartFiles, Long postIdx, AttachmentType attachmentType) {
        if (!hasExistUploadFile(multipartFiles)) {
            return;
        }
        List<Attachment> attachments = uploadedAttachments(multipartFiles, attachmentType);
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
    public List<Attachment> uploadedAttachments(MultipartFile[] multipartFiles, AttachmentType uploadType) {
        return Arrays.stream(multipartFiles)
                .limit(uploadType.getMaxUploadCount())
                .map(multipartFile -> convertToAttachment(AttachmentUtils.processServerUploadFile(multipartFile), uploadType))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * 첨부파일을 업로드 하고, 업로드된 첨부파일 목록을 반환
     *
     * @param multipartFiles 업로드할 {@link MultipartFile} 배열
     * @param uploadType     첨부파일 업로드 타입을 정의한 {@link AttachmentType} 객체
     * @return 업로드된 첨부파일 목록
     */
    public Attachment uploadedAttachment(MultipartFile multipartFiles, AttachmentType uploadType) {
        return convertToAttachment(AttachmentUtils.processServerUploadFile(multipartFiles), uploadType);
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
        if (indexesToDelete.isEmpty()) {
            saveAttachments(multipartFiles, postIdx, attachmentType);
            return;
        }

        indexesToDelete.removeAll(previouslyUploadedIndexes);
        deleteOnlyAttachments(indexesToDelete);
        saveAttachments(multipartFiles, postIdx, attachmentType);
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
     * 수정된 첨부파일과 대표 이미지 정보를 기반으로 첨부파일을 업데이트
     *
     * @param updateOption            업데이트 옵션을 정의한 {@link UpdateOption} 객체
     * @param addNewAttachments       추가할 새로운 첨부파일과 대표 이미지 정보를 담은 {@link AddNewAttachments} 객체
     * @param updateExistingAttachments  기존 첨부파일과 대표 이미지를 업데이트할 정보를 담은 {@link UpdateExistingAttachments} 객체
     * @param postIdx                 게시글 번호
     * @param attachmentType          첨부파일 업로드 타입을 정의한 {@link AttachmentType} 객체
     */
    public void updateAttachmentsWithVerifiedIndexes(UpdateOption updateOption,
                                                     AddNewAttachments addNewAttachments, UpdateExistingAttachments updateExistingAttachments,
                                                     Long postIdx, AttachmentType attachmentType) {
        if (UpdateOption.EDIT_EXISTING == updateOption) {
            // 기존에 첨부 파일이 없다면, 꺼지세요.
            List<Long> existingAttachmentIndexes = getAllIndexesByPostIdx(postIdx);
            if(existingAttachmentIndexes == null) {
                return;
            }

            UpdateExistingAttachments verifiedUpdateExistingAttachments = updateExistingAttachments.updateExistingAttachmentsAndThumbnail(
                    existingAttachmentIndexes, getThumbnailAttachmentIdxWithNullCheck(postIdx));

            // 2-1. 파일 삭제와 대표 이미지 변경(대표 이미지 변경/초기화 체크 X)
            if(verifiedUpdateExistingAttachments.hasAttachmentsToDeleteAndThumbnailToUpdate()) {
                deleteAttachmentsAndUpdateThumbnail(verifiedUpdateExistingAttachments, postIdx);
                return;
            }
            // 2-2. 대표이미지만 변경
            if (verifiedUpdateExistingAttachments.isChangedThumbnailIdx()) {
                updateOnlyThumbnail(verifiedUpdateExistingAttachments, postIdx);
                return;
            }
            // 2-3. 파일 삭제만(deleteOnlyAttachments)
            if (verifiedUpdateExistingAttachments.hasVerifiedDeletedAttachmentIndexes()) {
                verifiedUpdateExistingAttachments.getVerifiedDeletedAttachmentIndexes().forEach(this::deleteAttachment);
            }
        }
        if (UpdateOption.ADD_NEW == updateOption) {

            // 1. 새로운 대표 이미지 업로드와 이미지 업로드(uploadAndUpdateAttachments)
            if (addNewAttachments.isAllNotNull()) {
                saveAttachments(addNewAttachments.getNewAttachments(), postIdx, attachmentType);
                updateThumbnail(addNewAttachments.getNewThumbnail(), postIdx, attachmentType);
                return;
            }
            // 2. 새로운 대표 이미지만 업로드(uploadOnlyThumbnail)
            if (addNewAttachments.isNewThumbnailNotNull()) {
                updateThumbnail(addNewAttachments.getNewThumbnail(), postIdx, attachmentType);
                return;
            }
            // 3. 새로운 이미지만 업로드(uploadOnlyAttachments)
            if (addNewAttachments.isNewAttachmentsNotEmpty()) {
                saveAttachments(addNewAttachments.getNewAttachments(), postIdx, attachmentType);
            }
        }
    }


    /**
     * 지정된 게시글 번호에 대한 대표 이미지의 첨부 파일 인덱스를 가져옴.
     *
     * 존재하지 않는 경우 null을 반환
     *
     * @param postIdx 게시글 번호
     * @return 썸네일 첨부파일 인덱스 (썸네일이 없을 경우 null)
     */
    private Long getThumbnailAttachmentIdxWithNullCheck(Long postIdx) {
        ThumbNail thumbnail = getThumbnailByPostIdx(postIdx);
        return thumbnail != null ? thumbnail.getAttachmentIdx() : null;
    }

    /**
     * 수정된 첨부파일 정보를 기반으로 대표 이미지 업데이트
     *
     * 대표 이미지이 선택되어 있고, 업데이트가 가능한 경우에만 업데이트를 수행
     * 대표 이미지이 선택되어 있지 않은 경우 대표 이미지를 초기화
     *
     * @param updateExistingAttachments  기존 첨부파일과 대표 이미지 정보를 담은 {@link UpdateExistingAttachments} 객체
     * @param postIdx                 게시글 번호
     * @throws CannotUpdateThumbnailException 대표 이미지(썸네일) 업데이트가 불가능한 경우 발생하는 예외
     */
    private void updateOnlyThumbnail(UpdateExistingAttachments updateExistingAttachments, Long postIdx) {
        if(!updateExistingAttachments.isThumbnailNotSelected() && updateExistingAttachments.canUpdateThumbnail()) {
            attachmentRepository.updateThumbnail(Attachment.builder()
                    .attachmentIdx(updateExistingAttachments.getUnverifiedClientThumbnailAttachmentIdx())
                    .postIdx(postIdx)
                    .build());
        }
        if(updateExistingAttachments.isThumbnailNotSelected()) {
            attachmentRepository.clearThumbnail(postIdx);
            return;
        }
        if(!updateExistingAttachments.canUpdateThumbnail()) {
            throw new CannotUpdateThumbnailException(ErrorType.CAN_NOT_UPDATE_THUMBNAIL);
        }
    }

    /**
     * 수정된 첨부파일 정보를 기반으로 첨부파일과 대표 이미지를 업데이트
     *
     * 대표 이미지가 초기화되거나 변경되는 경우 해당 작업을 수행
     * 업데이트된 첨부파일들은 삭제하며, 이에 따른 데이터베이스 처리를 수행
     *
     * @param updateExistingAttachments  기존 첨부파일과 대표 이미지 정보를 담은 {@link UpdateExistingAttachments} 객체
     * @param postIdx                 게시글 번호
     * @throws CannotUpdateThumbnailException 대표 이미지(썸네일) 업데이트가 불가능한 경우 발생하는 예외
     */
    // TODO: 트랜잭션 고려
    private void deleteAttachmentsAndUpdateThumbnail(UpdateExistingAttachments updateExistingAttachments, Long postIdx) {

        if (updateExistingAttachments.isThumbnailNotSelected()) {
            attachmentRepository.clearThumbnail(postIdx);
        }

        if (!updateExistingAttachments.isThumbnailNotSelected() &&
                    updateExistingAttachments.isChangedThumbnailIdx()) {

            if(!updateExistingAttachments.canUpdateThumbnail()) {
                throw new CannotUpdateThumbnailException(ErrorType.CAN_NOT_UPDATE_THUMBNAIL);
            }

            if(updateExistingAttachments.canUpdateThumbnail()) {
                attachmentRepository.updateThumbnail(Attachment.builder()
                        .attachmentIdx(updateExistingAttachments.getUnverifiedClientThumbnailAttachmentIdx())
                        .postIdx(postIdx)
                        .build());
            }
        }
        // 파일삭제
        deleteOnlyAttachments(updateExistingAttachments.getVerifiedDeletedAttachmentIndexes());
    }

    /**
     * 첨부파일 번호 목록에 해당하는 첨부파일들을 삭제
     *
     * @param attachmentIndexes 첨부파일 번호 목록
     */
    public void deleteOnlyAttachments(List<Long> attachmentIndexes) {
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
}

