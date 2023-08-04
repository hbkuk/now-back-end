package com.now.config.fixtures.attachment;

import com.now.common.utils.AttachmentUtils;
import com.now.core.attachment.domain.Attachment;
import com.now.core.attachment.domain.wrapped.AttachmentExtension;
import com.now.core.attachment.domain.wrapped.AttachmentSize;
import com.now.core.attachment.domain.wrapped.OriginalAttachmentName;
import com.now.core.attachment.presentation.dto.AttachmentResponse;

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

    public static Attachment createAttachmentForMocking() {
        return Attachment.builder()
                .attachmentIdx(1L)
                .attachmentSize(new AttachmentSize(7777))
                .originalAttachmentName(new OriginalAttachmentName("MockAttachmentName.jpg"))
                .savedAttachmentName(AttachmentUtils.generateSystemName("MockAttachmentName.jpg"))
                .attachmentExtension(new AttachmentExtension(AttachmentUtils.extractFileExtension("MockAttachmentName.jpg")))
                .postIdx(1L)
                .build();
    }
}
