package com.now.core.post.domain;

import com.now.core.category.domain.constants.Category;
import com.now.core.post.domain.Notice;

import java.time.LocalDateTime;

public class NoticeTest {
    public static Notice createNotice(String managerId) {
        return Notice.builder()
                .postIdx(1L)
                .category(Category.EVENT)
                .title("제목")
                .managerId(managerId)
                .regDate(LocalDateTime.now())
                .modDate(LocalDateTime.now())
                .content("내용")
                .viewCount(0)
                .likeCount(0)
                .dislikeCount(0)
                .pinned(true)
                .build();
    }
}
