package com.now.core.post.community.presentation.dto;

import com.now.core.post.community.domain.Community;
import com.now.core.post.common.presentation.dto.Page;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 커뮤니티 게시글 목록과 페이지 객체를 담는 데이터 전송 객체
 */
@Slf4j
@Builder(toBuilder = true)
@Getter
@ToString
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class CommunitiesResponse {

    private final List<Community> communities;
    private final Page page;
}
