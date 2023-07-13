package com.now.core.post.domain;

import com.now.core.category.domain.constants.PostGroup;
import com.now.core.post.domain.abstractions.MemberPost;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * 커뮤니티 게시글을 나타내는 도메인 객체 
 */
@SuperBuilder(toBuilder = true)
@Getter
@ToString
@NoArgsConstructor(force = true)
public class Community extends MemberPost {
    private final PostGroup postGroup = PostGroup.COMMUNITY;

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
