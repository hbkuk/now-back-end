package com.now.core.post.presentation.dto;

import com.now.core.post.domain.Inquiry;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 문의 게시글 목록과 페이지 객체를 담는 데이터 전송 객체
 */
@Slf4j
@Builder(toBuilder = true)
@Getter
@ToString
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class InquiriesResponse {

    private final List<Inquiry> inquiries;
    private final Page page;
}
