package com.now.core.post.domain;

import com.now.core.category.domain.constants.PostGroup;
import com.now.core.post.domain.abstractions.MemberPost;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * 사진 게시글을 나타내는 도메인 객체
 */
@SuperBuilder(toBuilder = true)
@Getter
@ToString
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class Photo extends MemberPost {

    private final PostGroup postGroup = PostGroup.PHOTO;

    /**
     * 대표 사진으로 설정된 파일의 고유 식별자
     */
    private final Long thumbnailFileIdx;

    @Override
    public PostGroup getPostGroup() {
        return postGroup;
    }
}
