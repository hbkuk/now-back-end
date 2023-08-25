package com.now.core.comment.presentation.dto;

import com.now.core.comment.domain.Comment;
import com.now.core.post.common.presentation.dto.Page;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 댓글 목록을 담는 데이터 전송 객체
 */
@Slf4j
@Builder(toBuilder = true)
@Getter
@ToString
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class CommentsResponse {

    private final List<Comment> comments;
}
