package com.now.core.comment;

import com.now.core.comment.domain.Comment;

public class CommentTest {
    public static Comment createComment(String authorId) {
        return Comment.builder()
                .memberIdx(authorId)
                .content("contents")
                .memberPostIdx(1L)
                .build();
    }
}
