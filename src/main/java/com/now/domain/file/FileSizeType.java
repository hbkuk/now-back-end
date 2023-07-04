package com.now.domain.file;

/**
 * 파일 업로드 크기를 나타내는 상수값을 정의한 enum
 */
public enum FileSizeType {
    FILE(2048000),
    IMAGE(1024000);

    private final int fileSize;

    /**
     * UploadSize 상수에 대한 파일 크기를 설정
     *
     * @param fileSize 파일 크기 값
     */
    FileSizeType(int fileSize) {
        this.fileSize = fileSize;
    }

    /**
     * 파일 크기 값을 반환
     *
     * @return 파일 크기 값
     */
    public int getFileSize() {
        return fileSize;
    }
}
