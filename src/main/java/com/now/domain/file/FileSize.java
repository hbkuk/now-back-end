package com.now.domain.file;

/**
 * 파일 크기를 나타내는 원시값 포장 객체
 */
public class FileSize {
    private final int fileSize;

    /**
     * FileSize 객체를 생성
     *
     * @param value       파일 크기 값
     * @param fileSizeType  허용된 파일 크기를 나타내는 업로드 크기 상수
     * @throws IllegalArgumentException 파일 크기가 허용된 크기를 초과할 경우 발생하는 예외
     */
    public FileSize(int value, FileSizeType fileSizeType) {
        if (value > fileSizeType.getFileSize()) {
            throw new IllegalArgumentException("허용하지 않는 파일 크기입니다.");
        }
        this.fileSize = value;
    }
}
