package com.now.domain;

import com.now.domain.comment.Comment;

public class CommentTest {
    public static Comment createCommentByAuthorId(String authorId) {
        return Comment.builder()
                .authorId(authorId)
                .content("contents")
                .userPostIdx(1L)
                .build();
    }
}
