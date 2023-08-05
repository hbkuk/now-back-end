package com.now.config.fixtures.attachment;

import com.now.common.utils.AttachmentUtils;
import com.now.core.attachment.domain.Attachment;
import com.now.core.attachment.domain.wrapped.AttachmentExtension;
import com.now.core.attachment.domain.wrapped.AttachmentSize;
import com.now.core.attachment.domain.wrapped.OriginalAttachmentName;
import com.now.core.attachment.presentation.dto.AttachmentResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class AttachmentFixture {
    public static List<AttachmentResponse> createAttachments() {
        return List.of( createAttachmentResponse("sample1.png"));
    }

    public static AttachmentResponse createAttachmentResponse(String attachmentName) {
        return AttachmentResponse.builder()
                .attachmentIdx(1L)
                .attachmentSize(7777)
                .originalAttachmentName(attachmentName)
                .savedAttachmentName(AttachmentUtils.generateSystemName(attachmentName))
                .attachmentExtension(AttachmentUtils.extractFileExtension(attachmentName))
                .postIdx(1L)
                .build();
    }

    public static AttachmentResponse createAttachmentResponseByAttachmentIdx(Long attachmentIdx) {
        return AttachmentResponse.builder()
                .attachmentIdx(attachmentIdx)
                .attachmentSize(7777)
                .originalAttachmentName("sample1.png")
                .savedAttachmentName("sample1.png")
                .attachmentExtension(AttachmentUtils.extractFileExtension("sample1.png"))
                .postIdx(1L)
                .build();
    }

    public static AttachmentResponse createAttachmentResponseForBinaryDownload(String attachmentName) {
        return AttachmentResponse.builder()
                .attachmentIdx(1L)
                .attachmentSize(7777)
                .originalAttachmentName(attachmentName)
                .savedAttachmentName(attachmentName)
                .attachmentExtension(AttachmentUtils.extractFileExtension(attachmentName))
                .postIdx(1L)
                .build();
    }

    public static Attachment createAttachment(Long attachmentIdx, Long postIdx) {
        return Attachment.builder()
                .attachmentIdx(attachmentIdx)
                .postIdx(postIdx)
                .build();
    }

    public static MultipartFile createMockMultipartFile(String name) {
        return new MockMultipartFile("attachment", name, null, new byte[7777]);
    }
}
