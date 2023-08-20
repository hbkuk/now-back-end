package com.now.core.post.common.presentation.dto;

import com.now.core.post.community.domain.Community;
import com.now.core.post.inquiry.domain.Inquiry;
import com.now.core.post.notice.domain.Notice;
import com.now.core.post.photo.domain.Photo;
import lombok.*;

/**
 * 게시글 목록을 담는 데이터 전송 객체
 */
@Builder(toBuilder = true)
@Getter
@ToString
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class Posts {

    private final Notice notice;
    private final Community community;
    private final Photo photo;
    private final Inquiry inquiry;

}
