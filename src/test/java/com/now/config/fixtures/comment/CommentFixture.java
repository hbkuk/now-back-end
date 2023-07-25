package com.now.config.fixtures.comment;

import com.now.core.comment.domain.Comment;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class CommentFixture {

    public static List<Comment> createComments() {
        return List.of(createComment());
    }

    public static Comment createComment() {
        return Comment.builder()
                .commentIdx(1L)
                .memberNickname("Shark")
                .content("좋은 글 입니다.")
                .regDate(LocalDateTime.now().plus(1, ChronoUnit.DAYS))
                .postIdx(1L)
                .build();
    }

    public static Comment createCommentByMemberId(String memberId) {
        return Comment.builder()
                .memberId(memberId)
                .content("contents")
                .postIdx(1L)
                .build();
    }

    public static Comment createCommentForSave() {
        return Comment.builder()
                .memberNickname("Shark")
                .content("좋은 글 입니다.")
                .regDate(LocalDateTime.now().plus(1, ChronoUnit.DAYS))
                .postIdx(1L)
                .build();
    }

    public static Comment createCommentForUpdate(Long commentIdx) {
        return Comment.builder()
                .commentIdx(commentIdx)
                .memberNickname("Shark")
                .content("수정된 글 입니다.")
                .regDate(LocalDateTime.now().plus(5, ChronoUnit.DAYS))
                .postIdx(1L)
                .build();
    }
}
