package com.now.core.file.application.dto;

import lombok.*;

/**
 * 서버 디렉토리에 업로드된 파일 정보를 담고있는 객체
 */
@Builder(toBuilder = true)
@ToString
@Getter
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class UploadedFile {

    private final String systemName;
    private final String originalFileName;
    private final int fileSize;
}
