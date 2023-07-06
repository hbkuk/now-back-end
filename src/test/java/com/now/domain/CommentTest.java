package com.now.domain;

import com.now.domain.comment.Comment;

public class CommentTest {
    public static Comment createCommentByAuthorId(String authorId) {
        return Comment.builder()
                .authorId(authorId)
                .content("contents")
                .postIdx(1L)
                .build();
    }
}
