package com.now.domain.file;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

/**
 * 파일 확장자를 나타내는 원시값 포장 객체
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
public class FileExtension {
    private final String fileExtension;

    /**
     * Extension 객체 생성
     *
     * @param extension     파일 확장자
     * @param allowedExtensions 허용되는 확장자 그룹
     * @throws IllegalArgumentException 허용되지 않는 확장자일 경우 예외를 발생시킴
     */
    public FileExtension(String extension, List<String> allowedExtensions) {

        if(!allowedExtensions.contains(extension)) {
            throw new IllegalArgumentException("허용하지 않는 확장자입니다.");
        }
        this.fileExtension = extension;
    }
}
