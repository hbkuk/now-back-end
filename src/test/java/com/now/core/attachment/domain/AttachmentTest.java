package com.now.core.attachment.domain;

import com.now.common.utils.AttachmentUtils;
import com.now.core.attachment.presentation.dto.AttachmentResponse;

import java.util.List;

public class AttachmentTest {

    public static List<AttachmentResponse> createAttachments() {
        List<AttachmentResponse> attachments = List.of( createAttachment("sample1.png"));
        return attachments;
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
