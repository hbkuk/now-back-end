package com.now.domain.file;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 원본 파일 이름을 나타내는 원시값 포장 객체
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
public class OriginalFileName {
    private static final int MAX_VALUE_LENGTH = 500;
    private final String originalFileName;

    /**
     * OriginalName 객체 생성
     *
     * @param fileName 파일 이름
     * @throws IllegalArgumentException 파일 이름이 정해진 길이를 초과할 경우 예외를 발생시킴
     */
    public OriginalFileName(String fileName) {
        if (fileName.length() > MAX_VALUE_LENGTH) {
            throw new IllegalArgumentException("파일 이름은 500자를 초과할 수 없습니다.");
        }
        this.originalFileName = fileName;
    }
}
