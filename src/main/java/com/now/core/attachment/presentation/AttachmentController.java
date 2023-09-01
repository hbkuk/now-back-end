package com.now.core.attachment.presentation;

import com.now.common.utils.AttachmentUtils;
import com.now.core.attachment.application.AttachmentService;
import com.now.core.attachment.application.StorageService;
import com.now.core.attachment.presentation.dto.AttachmentResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Objects;

/**
 * 첨부파일 관련 작업을 위한 컨트롤러
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class AttachmentController {

    private final StorageService storageService;
    private final AttachmentService attachmentService;

    /**
     * 첨부파일 번호 해당하는 파일을 응답
     *
     * @param attachmentIdx 첨부파일 번호
     * @return 응답 결과
     */
    @GetMapping("/attachments/{attachmentIdx}")
    public ResponseEntity<byte[]> serveDownloadFile(@PathVariable("attachmentIdx") Long attachmentIdx) throws IOException {
        AttachmentResponse attachment = attachmentService.getAttachment(attachmentIdx);

        byte[] attachmentContent = AttachmentUtils.convertByteArray(storageService.createStream(attachment.getSavedAttachmentName()));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment",
                AttachmentUtils.generateEncodedName(attachment.getOriginalAttachmentName()));
        headers.setContentLength(Objects.requireNonNull(attachmentContent).length);

        return ResponseEntity.ok().headers(headers).body(attachmentContent);
    }

}
