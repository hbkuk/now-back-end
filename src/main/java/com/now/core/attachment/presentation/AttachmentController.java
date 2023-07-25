package com.now.core.attachment.presentation;

import com.now.common.utils.AttachmentUtils;
import com.now.core.attachment.application.AttachmentService;
import com.now.core.attachment.domain.Attachment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

/**
 * 첨부파일 관련 작업을 위한 컨트롤러
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class AttachmentController {

    private final AttachmentService attachmentService;

    /**
     * 첨부파일 번호 해당하는 파일을 응답
     *
     * @param attachmentIdx 첨부파일 번호
     * @return 응답 결과
     */
    @GetMapping("/attachments/{attachmentIdx}")
    public ResponseEntity<byte[]> serveDownloadFile(@PathVariable("attachmentIdx") Long attachmentIdx) {
        log.debug("serveDownloadFile 호출 -> 파일 번호 : {}", attachmentIdx);

        // 1. 파일 확인
        Attachment attachment = attachmentService.getAttachment(attachmentIdx);

        // 2. 파일을 바이트 배열로 변환
        byte[] attachmentContent = AttachmentUtils.convertByteArray(attachment.getSavedAttachmentName());

        // 3. 다운로드 응답 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment",
                AttachmentUtils.generateEncodedName(attachment.getOriginalAttachmentName().getOriginalAttachmentName()));
        headers.setContentLength(Objects.requireNonNull(attachmentContent).length);

        return ResponseEntity.ok().headers(headers).body(attachmentContent);
    }

}
