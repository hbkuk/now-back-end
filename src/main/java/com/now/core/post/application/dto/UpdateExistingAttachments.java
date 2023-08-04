package com.now.core.post.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@ToString()
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class UpdateExistingAttachments { 
    private final Long thumbnailAttachmentIdx;
    private final List<Long> notDeletedAttachmentIndexes;

    // 정적 팩토리 메서드 추가
    public static UpdateExistingAttachments of(Long thumbnailIdx, List<Long> previouslyUploadedIndexes) {
        return new UpdateExistingAttachments(thumbnailIdx, previouslyUploadedIndexes);
    }

    public boolean hasSelectedThumbnailIdx() {
        return thumbnailAttachmentIdx != 0;
    }
}
