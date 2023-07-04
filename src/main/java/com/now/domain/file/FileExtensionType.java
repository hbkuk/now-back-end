package com.now.domain.file;

import java.util.Arrays;
import java.util.List;

/**
 * 확장자 그룹을 나타내는 enum
 */
public enum FileExtensionType {
    FILE("jpg", "gif", "png", "zip"),
    IMAGE("jpg", "gif", "png");

    private final List<String> extensions;

    /**
     * 확장자 그룹을 초기화하는 생성자
     *
     * @param extensions 확장자 목록
     */
    FileExtensionType(String... extensions) {
        this.extensions = Arrays.asList(extensions);
    }

    /**
     * 해당 확장자 그룹의 확장자 목록을 반환
     *
     * @return 확장자 목록
     */
    public List<String> getExtensions() {
        return extensions;
    }
}
