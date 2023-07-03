package com.now.domain;

import com.now.domain.comment.Comment;

public class CommentTest {
    public static Comment newComment(String userId) {
        return Comment.builder()
                .userId(userId)
                .content("contents")
                .postIdx(1L)
                .build();
    }
}
