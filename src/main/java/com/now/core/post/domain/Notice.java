package com.now.core.post.domain;

import com.now.core.category.domain.constants.PostGroup;
import com.now.core.post.domain.abstractions.ManagerPost;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotNull;

/**
 * 공지사항 게시글을 나타내는 도메인 객체
 */
@SuperBuilder(toBuilder = true)
@Getter
@ToString
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class Notice extends ManagerPost {

    private final PostGroup postGroup = PostGroup.NOTICE;

    /**
     * 공지사항의 상단 고정 여부 (true: 상단 고정)
     */
    @NotNull(message = "핀 설정 필수")
    private final boolean isPinned;

    /**
     * 게시글 그룹을 반환
     *
     * @return 게시글 그룹
     */
    @Override
    public PostGroup getPostGroup() {
        return postGroup;
    }
}
