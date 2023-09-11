package com.now.core.attachment.domain.constants;

import lombok.Getter;

import java.util.List;

/**
 * 파일 업로드 타입을 나타내는 enum
 */
@Getter
public enum AttachmentType {
    FILE(List.of("jpg", "gif", "png", "jpeg", "psd", "tiff", "heif", "zip", "docx", "xlsx", "pptx"), 2048000, 5),
    IMAGE(List.of("jpg", "gif", "png", "jpeg", "psd", "tiff", "heif"), 2048000, 20);

    private final List<String> allowedExtensions;
    private final int maxUploadSize;
    private final int maxUploadCount;

    /**
     * UploadType 객체를 초기화하는 생성자
     *
     * @param allowedExtensions 허용하는 파일 확장자 목록
     * @param maxUploadSize 파일의 최대 업로드 크기 (바이트 단위)
     * @param maxUploadCount 최대 업로드 횟수
     */
    AttachmentType(List<String> allowedExtensions, int maxUploadSize, int maxUploadCount) {
        this.allowedExtensions = allowedExtensions;
        this.maxUploadSize = maxUploadSize;
        this.maxUploadCount = maxUploadCount;
    }
}

