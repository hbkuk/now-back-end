package com.now.core.attachment.application;

import com.now.core.attachment.application.dto.UploadedAttachment;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

public interface StorageService {

    /**
     * 파일 업로드
     *
     * @param multipartFile 업로드할 파일
     * @return 업로드된 파일 정보
     */
    UploadedAttachment upload(MultipartFile multipartFile);

    /**
     * 파일 삭제
     *
     * @param attachmentName 삭제할 첨부파일 이름
     * @return 삭제 성공 여부
     */
    boolean delete(String attachmentName);


    /**
     * 첨부파일 기반 스트림 생성
     *
     * @param attachmentName 읽어올 첨부파일의 이름
     * @return 첨부파일의 내용을 담은 InputStream 객체
     * @throws IOException 파일을 읽어오는 도중 발생한 입출력 예외
     */
    InputStream createStream(String attachmentName) throws IOException;
}