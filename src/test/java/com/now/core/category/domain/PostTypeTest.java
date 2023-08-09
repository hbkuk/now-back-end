package com.now.core.category.domain;

import com.now.core.category.domain.constants.Category;
import com.now.core.category.domain.constants.PostGroup;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("게시글 타입은")
public class PostTypeTest {

    @Test
    @DisplayName("Category의 EVENT는 PostGroup의 NOTICE에 속한다.")
    void find_post_type_1() {
        PostGroup postType = PostGroup.findByCategory(Category.EVENT);

        assertThat(postType).isEqualTo(PostGroup.NOTICE);
    }

    @Test
    @DisplayName("Category의 LIFESTYLE은 PostGroup의 COMMUNITY에 속한다.")
    void find_post_type_2() {
        PostGroup postType = PostGroup.findByCategory(Category.LIFESTYLE);

        assertThat(postType).isEqualTo(PostGroup.COMMUNITY);
    }

    @Test
    @DisplayName("Category의 DAILY_LIFE는 PostGroup의 PHOTO에 속한다.")
    void find_post_type_3() {
        PostGroup postType = PostGroup.findByCategory(Category.DAILY_LIFE);

        assertThat(postType).isEqualTo(PostGroup.PHOTO);
    }

    @Test
    @DisplayName("Category의 SERVICE는 PostGroup의 INQUIRY에 속한다.")
    void find_post_type_4() {
        PostGroup postType = PostGroup.findByCategory(Category.SERVICE);

        assertThat(postType).isEqualTo(PostGroup.INQUIRY);
    }

    @Test
    @DisplayName("isCategoryInGroup 메서드는 전달한 카테고리가 전달한 그룹에 포함된다면 true, 그렇지 않다면 false 반환")
    void isCategoryInGroup_return_true_1() {
        assertThat(PostGroup.isCategoryInGroup(PostGroup.NOTICE, Category.EVENT)).isTrue();
        assertThat(PostGroup.isCategoryInGroup(PostGroup.NOTICE, Category.NEWS)).isTrue();
        assertThat(PostGroup.isCategoryInGroup(PostGroup.PHOTO, Category.EVENT)).isFalse();
        assertThat(PostGroup.isCategoryInGroup(PostGroup.COMMUNITY, Category.NEWS)).isFalse();
    }
}
