package com.now.core.post.notice.presentation.dto;

import com.now.core.post.notice.domain.Notice;
import com.now.core.post.common.presentation.dto.Page;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 공지 게시글 목록과 페이지 객체를 담는 데이터 전송 객체
 */
@Slf4j
@Builder(toBuilder = true)
@Getter
@ToString
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class NoticesResponse {

    private final List<Notice> notices;
    private final Page page;
}