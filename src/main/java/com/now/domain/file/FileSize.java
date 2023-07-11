package com.now.domain.file;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 파일 크기를 나타내는 원시값 포장 객체
 *
 * @ToString
 *     : 객체의 문자열 표현을 자동으로 생성합니다. 주요 필드들의 값을 포함한 문자열을 반환합니다.
 * @Getter
 *     : 필드들에 대한 Getter 메서드를 자동으로 생성합니다.
 * @NoArgsConstructor(force = true)
 *     : 매개변수가 없는 기본 생성자를 자동으로 생성합니다. MyBatis 또는 JPA 라이브러리에서는 기본 생성자를 필요로 합니다.
 * @AllArgsConstructor
 *     : 모든 필드를 매개변수로 받는 생성자를 자동으로 생성합니다.
 */
@ToString
@Getter
@NoArgsConstructor(force = true)
@AllArgsConstructor
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
