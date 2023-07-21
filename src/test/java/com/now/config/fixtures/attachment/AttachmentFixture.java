package com.now.config.fixtures.attachment;

import com.now.common.utils.AttachmentUtils;
import com.now.core.attachment.presentation.dto.AttachmentResponse;

import java.util.List;

public class AttachmentFixture {
    public static List<AttachmentResponse> createAttachments() {
        return List.of( createAttachment("sample1.png"));
    }

    public static AttachmentResponse createAttachment(String attachmentName) {
        AttachmentResponse attachment = AttachmentResponse.builder()
                .attachmentIdx(1L)
                .attachmentSize(7777)
                .originalAttachmentName(attachmentName)
                .attachmentExtension(AttachmentUtils.extractFileExtension(attachmentName))
                .memberPostIdx(1L)
                .build();
        return attachment;
    }
}
