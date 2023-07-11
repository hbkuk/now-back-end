package com.now.domain.file;

/**
 * 파일 크기를 나타내는 원시값 포장 객체
 */
public class FileSize {
    private final int fileSize;

    /**
     * FileSize 객체를 생성
     *
     * @param fileSize       파일 크기 값
     * @param maxFileSize  허용되는 최대 파일 크기
     * @throws IllegalArgumentException 파일 크기가 허용된 크기를 초과할 경우 발생하는 예외
     */
    public FileSize(int fileSize, int maxFileSize) {
        if (fileSize > maxFileSize) {
            throw new IllegalArgumentException("허용하지 않는 파일 크기입니다.");
        }
        this.fileSize = fileSize;
    }
}
