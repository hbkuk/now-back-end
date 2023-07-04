package com.now.domain.file;

/**
 * 파일 확장자를 나타내는 원시값 포장 객체
 */
public class FileExtension {
    private final String extension;

    /**
     * Extension 객체 생성
     *
     * @param value     파일 확장자
     * @param extensionType 허용된 확장자 그룹
     * @throws IllegalArgumentException 허용되지 않는 확장자일 경우 예외를 발생시킴
     */
    public FileExtension(String value, FileExtensionType extensionType) {

        if(!extensionType.getExtensions().contains(value)) {
            throw new IllegalArgumentException("허용하지 않는 확장자입니다.");
        }
        this.extension = value;
    }
}
