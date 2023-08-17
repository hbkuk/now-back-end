package com.now.core.post.application.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 업데이트 대상 첨부 파일 정보를 나타내는 DTO
 */
@Builder
@Getter
@ToString
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class UpdateExistingAttachments {

    private final Long unverifiedClientThumbnailAttachmentIdx; // 아직 검증되지 않은 클라이언트로부터 받은 대표 이미지 첨부 파일 인덱스

    private final List<Long> unverifiedClientExcludedIndexes; // 아직 검증되지 않은 클라이언트로부터 받은 삭제하지 않는 첨부 파일 인덱스 목록


    private Long existingThumbnailAttachmentIdx; // 실제 존재하는 대표 이미지의 번호

    private List<Long> existingAttachmentIndexes; // 실제 존재하는 파일 인덱스 목록

    private List<Long> verifiedDeletedAttachmentIndexes; // 검증 완료된 삭제할 첨부 파일 인덱스 목록

    /**
     * 정적 팩토리 메서드
     *
     * @param thumbnailIdx              대표 이미지 첨부 파일 인덱스
     * @param previouslyUploadedIndexes 아직 검증되지 않은 클라이언트로부터 받은 삭제하지 않는 첨부 파일 인덱스 목록
     * @return UpdateExistingAttachments  업데이트 대상 첨부 파일 정보 객체
     */
    public static UpdateExistingAttachments of(Long thumbnailIdx, List<Long> previouslyUploadedIndexes) {
        return new UpdateExistingAttachments(thumbnailIdx, previouslyUploadedIndexes,
                null, null, null);
    }

    /**
     * 검증 완료된 삭제할 첨부 파일 인덱스와 실제 존재하는 파일 인덱스 목록을 업데이트
     *
     * @param existingAttachmentIndexes 현재 존재하는 첨부 파일 인덱스 목록
     */
    public UpdateExistingAttachments updateExistingAttachmentsAndThumbnail(List<Long> existingAttachmentIndexes, Long existingThumbnailAttachmentIdx) {
        List<Long> verifiedDeletedAttachmentIndexes = new ArrayList<>(existingAttachmentIndexes);
        verifiedDeletedAttachmentIndexes.removeAll(this.unverifiedClientExcludedIndexes);

        this.existingAttachmentIndexes = existingAttachmentIndexes;
        this.verifiedDeletedAttachmentIndexes = verifiedDeletedAttachmentIndexes;
        this.existingThumbnailAttachmentIdx = existingThumbnailAttachmentIdx;
        return this;
    }

    /**
     * 대표 이미지를 업데이트해야하는 경우 true, 그렇지 않은 경우 false
     *
     * @return boolean  대표 이미지를 업데이트해야하는 경우 true, 그렇지 않은 경우 false
     */
    public boolean isChangedThumbnailIdx() {
        return !Objects.equals(this.unverifiedClientThumbnailAttachmentIdx, this.existingThumbnailAttachmentIdx);
    }

    /**
     * 검증 완료된 삭제할 첨부 파일이 존재하는 경우 true, 그렇지 않은 경우 false
     *
     * @return boolean  삭제할 첨부 파일이 존재하는 경우 true, 그렇지 않은 경우 false
     */
    public boolean hasVerifiedDeletedAttachmentIndexes() {
        return !verifiedDeletedAttachmentIndexes.isEmpty();
    }

    /**
     * 대표 이미지를 업데이트하고 삭제할 첨부 파일이 존재하는 경우 true, 그렇지 않은 경우 false
     *
     * @return boolean  대표 이미지를 업데이트하고 삭제할 첨부 파일이 존재하는 경우 true, 그렇지 않은 경우 false
     */
    public boolean hasAttachmentsToDeleteAndThumbnailToUpdate() {
        return hasVerifiedDeletedAttachmentIndexes() && isChangedThumbnailIdx();
    }

    /**
     * 대표 이미지를 업데이트할 수 있는 경우 true, 그렇지 않은 경우 false
     *
     * @return boolean  대표 이미지를 업데이트할 수 있는 경우 true, 그렇지 않은 경우 false
     */
    public boolean canUpdateThumbnail() {
        return !this.verifiedDeletedAttachmentIndexes.contains(this.unverifiedClientThumbnailAttachmentIdx) &&
                this.existingAttachmentIndexes.contains(this.unverifiedClientThumbnailAttachmentIdx);
    }

    /**
     * 클라이언트로부터 대표 이미지 첨부 파일을 선택하지 않았는지 확인합니다.
     *
     * @return true if the thumbnail is not selected, false otherwise.
     */
    public boolean isThumbnailNotSelected() {
        return unverifiedClientThumbnailAttachmentIdx == null || unverifiedClientThumbnailAttachmentIdx == 0;
    }
}
