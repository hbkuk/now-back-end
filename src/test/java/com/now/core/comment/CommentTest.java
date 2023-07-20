package com.now.core.comment;

import com.now.core.comment.domain.Comment;

import java.util.List;

public class CommentTest {

    public static List<Comment> createComments() {
        List<Comment> comments = List.of(createComment("tester"));
        return comments;
    }

    public static Comment createComment(String memberId) {
        return Comment.builder()
                .memberIdx(memberId)
                .content("contents")
                .memberPostIdx(1L)
                .build();
    }
}
