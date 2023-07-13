package com.now.domain.post;

import com.now.common.mapper.EnumMapperValue;
import com.now.core.category.domain.constants.Category;
import com.now.core.category.domain.constants.PostGroup;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class PostTypeTest {

    @Test
    @DisplayName("카테고리 전체 출력")
    void create() {
        List<EnumMapperValue> categories = Arrays.stream(Category.values())
                .map(EnumMapperValue::new)
                .collect(Collectors.toList());

        System.out.println(categories);
    }

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
        assertThat(PostGroup.isCategoryInGroup(Category.EVENT, PostGroup.NOTICE)).isTrue();
        assertThat(PostGroup.isCategoryInGroup(Category.NEWS, PostGroup.NOTICE)).isTrue();
        assertThat(PostGroup.isCategoryInGroup(Category.EVENT, PostGroup.PHOTO)).isFalse();
        assertThat(PostGroup.isCategoryInGroup(Category.NEWS, PostGroup.COMMUNITY)).isFalse();
    }
}
