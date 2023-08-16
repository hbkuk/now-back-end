package com.now.core.post.application.integrated;

import com.now.core.attachment.application.AttachmentService;
import com.now.core.attachment.domain.constants.AttachmentType;
import com.now.core.comment.application.CommentService;
import com.now.core.post.application.CommunityService;
import com.now.core.post.application.dto.AddNewAttachments;
import com.now.core.post.application.dto.UpdateExistingAttachments;
import com.now.core.post.domain.Community;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CommunityIntegratedService {

    private final CommunityService communityService;
    private final AttachmentService attachmentService;
    private final CommentService commentService;

    public void registerCommunity(Community community, MultipartFile[] attachments) {
        communityService.registerCommunity(community);
        attachmentService.saveAttachments(attachments, community.getPostIdx(), AttachmentType.FILE);
    }

    public void updateCommunity(Community updatedCommunity,
                                AddNewAttachments addNewAttachments, UpdateExistingAttachments updateExistingAttachments) {
        communityService.hasUpdateAccess(updatedCommunity.getPostIdx(), updatedCommunity.getMemberId());

        communityService.updateCommunity(updatedCommunity);
        attachmentService.updateAttachments(addNewAttachments, updateExistingAttachments,
                updatedCommunity.getPostIdx(), AttachmentType.FILE);
    }

    public void deleteCommunity(Long postIdx, String memberId) {
        communityService.hasDeleteAccess(postIdx, memberId);

        commentService.deleteAllByPostIdx(postIdx);
        attachmentService.deleteAllByPostIdx(postIdx);
        communityService.deleteCommunity(postIdx);
    }

}
