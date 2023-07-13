package com.now.domain.post;

import com.now.core.post.domain.Photo;

import java.time.LocalDateTime;

public class PhotoTest {
    public static Photo createNoticeByManagerId(String memberId) {
        return Photo.builder()
                .postIdx(1L)
                .title("제목")
                .memberId(memberId)
                .regDate(LocalDateTime.now())
                .modDate(LocalDateTime.now())
                .content("내용")
                .viewCount(0)
                .likeCount(0)
                .dislikeCount(0)
                .build();
    }
}
