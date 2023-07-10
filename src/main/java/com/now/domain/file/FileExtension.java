package com.now.domain.file;

import java.util.List;

/**
 * 파일 확장자를 나타내는 원시값 포장 객체
 */
public class FileExtension {
    private final String fileExtension;

    /**
     * Extension 객체 생성
     *
     * @param value     파일 확장자
     * @param allowedExtensions 허용되는 확장자 그룹
     * @throws IllegalArgumentException 허용되지 않는 확장자일 경우 예외를 발생시킴
     */
    public FileExtension(String value, List<String> allowedExtensions) {

        if(!allowedExtensions.contains(value)) {
            throw new IllegalArgumentException("허용하지 않는 확장자입니다.");
        }
        this.fileExtension = value;
    }
}
