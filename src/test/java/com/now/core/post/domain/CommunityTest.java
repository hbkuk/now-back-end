package com.now.core.post.domain;

import com.now.core.attachment.presentation.dto.AttachmentResponse;
import com.now.core.category.domain.constants.Category;
import com.now.core.comment.domain.Comment;

import java.time.LocalDateTime;
import java.util.List;

public class CommunityTest {
    public static Community createCommunity(String memberId) {
        return Community.builder()
                .postIdx(1L)
                .title("제목")
                .memberId(memberId)
                .regDate(LocalDateTime.now())
                .modDate(LocalDateTime.now())
                .content("내용")
                .viewCount(0)
                .likeCount(0)
                .dislikeCount(0)
                .category(Category.COMMUNITY_STUDY)
                .build();
    }

    public static Community createCommunity(String memberId, Category category) {
        return Community.builder()
                .postIdx(1L)
                .title("제목")
                .memberId(memberId)
                .regDate(LocalDateTime.now())
                .modDate(LocalDateTime.now())
                .content("내용")
                .viewCount(0)
                .likeCount(0)
                .dislikeCount(0)
                .category(category)
                .build();
    }

    public static Community createCommunity(List<AttachmentResponse> attachments, List<Comment> comments) {
        return Community.builder()
                .postIdx(1L)
                .title("제목")
                .memberId("tester")
                .memberNickname("테스터")
                .regDate(LocalDateTime.now())
                .modDate(LocalDateTime.now())
                .content("내용")
                .viewCount(0)
                .likeCount(0)
                .dislikeCount(0)
                .category(Category.COMMUNITY_STUDY)
                .attachments(attachments)
                .comments(comments)
                .build();
    }
}
