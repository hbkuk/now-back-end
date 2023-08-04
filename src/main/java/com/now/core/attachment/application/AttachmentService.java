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
import com.now.core.attachment.presentation.dto.AttachmentResponse;
import com.now.core.post.application.dto.AddNewAttachments;
import com.now.core.post.application.dto.UpdateExistingAttachments;
import com.now.core.post.domain.constants.UpdateOption;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
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
     * @param thumbnailAttachmentIdx 대표 이미지로 설정할 기존 첨부파일 번호
     * @param multipartFiles         MultipartFile[] 객체
     * @param postIdx                게시글 번호
     * @param attachmentType         첨부파일 업로드 타입
     */
    public void saveAttachments(Long thumbnailAttachmentIdx, MultipartFile[] multipartFiles, Long postIdx, AttachmentType attachmentType) {
        if (!hasExistUploadFile(multipartFiles) && thumbnailAttachmentIdx != null) {
            attachmentRepository.saveThumbNail(attachmentRepository.findAttachmentByAttachmentIdx(thumbnailAttachmentIdx));
            return;
        }
        List<Attachment> attachments = uploadedAttachments(multipartFiles, attachmentType);
        attachments.forEach(attachment -> saveAttachment(attachment.updateMemberPostIdx(postIdx)));
        attachmentRepository.saveThumbNail(attachmentRepository.findAttachmentByAttachmentIdx(thumbnailAttachmentIdx));
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
     * 첨부파일을 서버 디렉토리에 업로드 후 데이터베이스에 첨부파일 저장
     *
     * @param multipartFile  MultipartFile 객체
     * @param postIdx        게시글 번호
     * @param attachmentType 첨부파일 업로드 타입
     */
    public void saveAttachment(MultipartFile multipartFile, Long postIdx, AttachmentType attachmentType) {
        if (multipartFile.isEmpty()) {
            return;
        }
        Attachment attachment = uploadedAttachment(multipartFile, attachmentType);
        saveAttachment(attachment.updateMemberPostIdx(postIdx));
    }

    /**
     * 첨부파일을 서버 디렉토리에 업로드 후 데이터베이스에 첨부파일 저장
     *
     * @param multipartFile  MultipartFile 객체
     * @param postIdx        게시글 번호
     * @param attachmentType 첨부파일 업로드 타입
     */
    public void saveThumbnail(MultipartFile multipartFile, Long postIdx, AttachmentType attachmentType) {
        if (multipartFile.isEmpty()) {
            return;
        }
        Attachment thumbNailAttachment = uploadedAttachment(multipartFile, attachmentType);
        attachmentRepository.saveThumbNail(thumbNailAttachment.updateMemberPostIdx(postIdx));
    }

    // TODO: javadoc
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

    public void saveAttachmentsWithThumbnail(AddNewAttachments addNewAttachments, Long postIdx, AttachmentType attachmentType) {
        if (!hasExistUploadFile(addNewAttachments.getAllMultipartFiles())) {
            return;
        }
        List<Attachment> attachments = uploadedAttachments(addNewAttachments.getNewAttachments(), attachmentType);
        attachments.forEach(attachment -> saveAttachment(attachment.updateMemberPostIdx(postIdx)));

        Attachment thumbNailAttachment = uploadedAttachment(addNewAttachments.getNewThumbnail(), attachmentType);
        saveAttachment(thumbNailAttachment.updateMemberPostIdx(postIdx));
        attachmentRepository.saveThumbNail(thumbNailAttachment);
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


    public void updateAttachmentsWithThumbnail(UpdateOption updateOption,
                                               AddNewAttachments addNewAttachments, UpdateExistingAttachments updateExistingAttachments,
                                               Long postIdx, AttachmentType attachmentType) {
        if (UpdateOption.EDIT_EXISTING == updateOption) {
            updateExistingAttachments(updateExistingAttachments, postIdx);
        }
        if (UpdateOption.ADD_NEW == updateOption) {
            if (addNewAttachments.isAllNotNull()) { // 1. 새로운 대표 이미지 업로드와 이미지 업로드
                uploadAndUpdateAttachments(addNewAttachments, postIdx, attachmentType);
            }

            if (addNewAttachments.isNewThumbnailNotNull()) { // 2. 새로운 대표 이미지만 업로드
                uploadOnlyThumbnail(addNewAttachments, postIdx, attachmentType);
            }

            if (addNewAttachments.isNewAttachmentsNotEmpty()) { // 3. 새로운 이미지 업로드
                uploadOnlyAttachments(addNewAttachments, postIdx, attachmentType);
            }
        }
    }

    private void uploadOnlyAttachments(AddNewAttachments addNewAttachments, Long postIdx, AttachmentType attachmentType) {
        saveAttachments(addNewAttachments.getNewAttachments(), postIdx, attachmentType);
    }

    private void uploadOnlyThumbnail(AddNewAttachments addNewAttachments, Long postIdx, AttachmentType attachmentType) {
        // 1-1. 기존에 대표 이미지가 업로드 되지 않았다면
        if (attachmentRepository.findThumbnailByPostIdx(postIdx) == null) {
            saveThumbnail(addNewAttachments.getNewThumbnail(), postIdx, attachmentType);
        }
        // 1-2. 기존에 대표 이미지가 업로드 되었다면
        if (attachmentRepository.findThumbnailByPostIdx(postIdx) != null) {
            updateThumbnail(addNewAttachments.getNewThumbnail(), postIdx, attachmentType);
            ;
        }
    }

    private void uploadAndUpdateAttachments(AddNewAttachments addNewAttachments, Long postIdx, AttachmentType attachmentType) {
        // 1-1. 기존에 대표 이미지가 업로드 되지 않았다면
        if (attachmentRepository.findThumbnailByPostIdx(postIdx) == null) {
            saveAttachments(addNewAttachments.getNewAttachments(), postIdx, attachmentType);
            saveThumbnail(addNewAttachments.getNewThumbnail(), postIdx, attachmentType);
        }

        // 1-2. 기존에 대표 이미지가 업로드 되었다면
        if (attachmentRepository.findThumbnailByPostIdx(postIdx) != null) {
            saveAttachments(addNewAttachments.getNewAttachments(), postIdx, attachmentType);
            updateThumbnail(addNewAttachments.getNewThumbnail(), postIdx, attachmentType);
        }
    }

    public void updateExistingAttachments(UpdateExistingAttachments updateExistingAttachments, Long postIdx) {
        List<Long> existingAttachmentIndexes = getAllIndexesByPostIdx(postIdx);
        if (existingAttachmentIndexes.isEmpty()) {
            return;
        }

        List<Long> deleteAttachmentIndexes = new ArrayList<>(existingAttachmentIndexes); // 삭제할 파일 목록
        List<Long> notDeletedAttachmentIndexes = updateExistingAttachments.getNotDeletedAttachmentIndexes();
        deleteAttachmentIndexes.removeAll(notDeletedAttachmentIndexes);

        // 2-1. 파일 삭제와 대표 이미지 변경
        if (!deleteAttachmentIndexes.isEmpty() &&
                !Objects.equals(updateExistingAttachments.getThumbnailAttachmentIdx(), getThumbnailByPostIdx(postIdx).getThumbNailIdx())) {

            // 2-2. 파일 삭제 및 대표 이미지 변경(초기화)
            if(!deleteAttachmentIndexes.contains(getThumbnailByPostIdx(postIdx).getThumbNailIdx())) {
                deleteAttachmentsAndUpdateThumbnail(updateExistingAttachments, postIdx, existingAttachmentIndexes, deleteAttachmentIndexes, notDeletedAttachmentIndexes);
            }

            // 2-2. 파일 삭제 + 대표이미지 삭제 + 대표 이미지 초기화
            if(deleteAttachmentIndexes.contains(getThumbnailByPostIdx(postIdx).getThumbNailIdx())) {
                deleteAttachmentsAndClearThumbnail(postIdx, deleteAttachmentIndexes);
            }
        }


        // 2-3. 대표이미지만 변경
        if (deleteAttachmentIndexes.isEmpty() &&
                !Objects.equals(updateExistingAttachments.getThumbnailAttachmentIdx(), getThumbnailByPostIdx(postIdx).getThumbNailIdx())) {

            // 2-3-1. 대표이미지 초기화
            if(updateExistingAttachments.getThumbnailAttachmentIdx() == 0) {
                attachmentRepository.clearThumbnail(postIdx);
            }

            // 2-3-2. 대표이미지 변경
            if(attachmentRepository.findAllIndexesByPostIdx(postIdx).contains(updateExistingAttachments.getThumbnailAttachmentIdx())) {
                attachmentRepository.updateThumbnail(Attachment.builder()
                                .attachmentIdx(updateExistingAttachments.getThumbnailAttachmentIdx())
                                .postIdx(postIdx)
                                .build());
            }

            // 2-3-3. 미친놈이 이상한걸로 바꾼다면 초기화
            if(!attachmentRepository.findAllIndexesByPostIdx(postIdx).contains(updateExistingAttachments.getThumbnailAttachmentIdx())) {
                attachmentRepository.clearThumbnail(postIdx);
            }
        }

        // 2-4. 파일 삭제만
        if (!deleteAttachmentIndexes.isEmpty() &&
                Objects.equals(updateExistingAttachments.getThumbnailAttachmentIdx(), getThumbnailByPostIdx(postIdx).getThumbNailIdx())) {
            deleteOnlyAttachments(deleteAttachmentIndexes);
        }
    }

    private void updateOnlyThumbnail(UpdateExistingAttachments updateExistingAttachments, Long postIdx) {
        // 2-3-1. 대표 이미지를 선택하지 않음.
        if (updateExistingAttachments.getThumbnailAttachmentIdx() == 0) {
            attachmentRepository.clearThumbnail(postIdx);
        }

        // 2-3-2. 대표 이미지를 변경함.
        if (updateExistingAttachments.getThumbnailAttachmentIdx() == 0) {
            attachmentRepository.updateThumbnail(Attachment.builder()
                    .attachmentIdx(updateExistingAttachments.getThumbnailAttachmentIdx())
                    .postIdx(postIdx)
                    .build());
        }
    }

    private void deleteAttachmentsAndUpdateThumbnail(UpdateExistingAttachments updateExistingAttachments, Long postIdx,
                                                     List<Long> existingAttachmentIndexes, List<Long> deleteAttachmentIndexes,
                                                     List<Long> notDeletedAttachmentIndexes) {
        // 2-1-1. 대표 이미지를 선택하지 않음.
        if (!updateExistingAttachments.hasSelectedThumbnailIdx()) {
            attachmentRepository.clearThumbnail(postIdx);
        }

        // 2-1-2. 대표 이미지를 변경함.
        if (updateExistingAttachments.hasSelectedThumbnailIdx()) {

            // 2-1-2-1. 삭제되지 않은 인덱스를 확인
            List<Long> notDeletedIndexes = existingAttachmentIndexes.stream()
                    .filter(idx -> !notDeletedAttachmentIndexes.contains(idx))
                    .collect(Collectors.toList());

            // 미친놈이. 이상한걸로 바꾸려고 한다면 .. thumbnail을 초기화
            if (!notDeletedIndexes.contains(updateExistingAttachments.getThumbnailAttachmentIdx())) {
                attachmentRepository.clearThumbnail(postIdx);
            }

            // 대표 이미지 업데이트
            if (notDeletedIndexes.contains(updateExistingAttachments.getThumbnailAttachmentIdx())) {
                attachmentRepository.updateThumbnail(Attachment.builder()
                        .attachmentIdx(updateExistingAttachments.getThumbnailAttachmentIdx())
                        .postIdx(postIdx)
                        .build());
            }
        }
        // 파일삭제
        deleteOnlyAttachments(deleteAttachmentIndexes);
    }

    private void deleteAttachmentsAndClearThumbnail(Long postIdx, List<Long> deleteAttachmentIndexes) {
        // 대표이미지 클리어
        attachmentRepository.clearThumbnail(postIdx);

        // 파일삭제
        deleteOnlyAttachments(deleteAttachmentIndexes);
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

