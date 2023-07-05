package com.now.domain.file;

/**
 * 원본 파일 이름을 나타내는 원시값 포장 객체
 */
public class OriginalFileName {
    private final String originalFileName;

    /**
     * OriginalName 객체 생성
     *
     * @param value 원본 파일 이름
     * @throws IllegalArgumentException 파일 이름이 500자를 초과할 경우 예외를 발생시킴
     */
    public OriginalFileName(String value) {
        if (value.length() > 500) {
            throw new IllegalArgumentException("파일 이름은 500자를 초과할 수 없습니다.");
        }
        this.originalFileName = value;
    }
}
