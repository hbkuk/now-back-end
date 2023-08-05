package com.now.core.post.application.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;

@Builder
@Getter
@ToString
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class AddNewAttachments {
    private final MultipartFile newThumbnail;
    private final MultipartFile[] newAttachments;

    // 정적 팩토리 메서드 추가
    public static AddNewAttachments of(MultipartFile newThumbnail, MultipartFile[] newAttachments) {
        return new AddNewAttachments(newThumbnail, newAttachments);
    }

    // MultipartFile[]와 MultipartFile을 합치는 메서드 추가
    public MultipartFile[] getAllMultipartFiles() {

        int totalLength = newAttachments.length + (newThumbnail != null ? 1 : 0);
        MultipartFile[] allMultipartFiles = Arrays.copyOf(newAttachments, totalLength);
        if (newThumbnail != null) {
            allMultipartFiles[totalLength - 1] = newThumbnail;
        }
        return allMultipartFiles;
    }

    public boolean hasOnlyNewThumbnail() {
        return newThumbnail != null;
    }

    public boolean hasNewAttachmentsWithoutNewThumbnail() {
        return newAttachments != null && newAttachments.length > 0;
    }

    public boolean hasNewAttachments() {
        return hasOnlyNewThumbnail() && hasNewAttachmentsWithoutNewThumbnail();
    }
}
