package com.now.config.fixtures.post.dto;

import com.now.core.category.domain.constants.Category;
import com.now.core.category.domain.constants.PostGroup;
import com.now.core.post.common.presentation.dto.Condition;
import com.now.core.post.common.presentation.dto.constants.Sort;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ConditionFixture {

    public static Condition createCondition(Category category) {
        return Condition.builder()
                .startDate(formatLocalDateTime(LocalDateTime.now().minusYears(2)))
                .endDate(formatLocalDateTime(LocalDateTime.now()))
                .category(category)
                .keyword("input Keyword...")
                .sort(Sort.LATEST)
                .maxNumberOfPosts(10)
                .pageNo(1)
                .build();
    }

    public static Condition createConditionOnlySort(Sort sort) {
        return Condition.builder()
                .sort(sort)
                .build();
    }

    public static Condition createCondition(Sort sort, Integer maxNumberOfPosts) {
        return Condition.builder()
                .sort(sort)
                .maxNumberOfPosts(maxNumberOfPosts)
                .build();
    }

    public static Condition createCondition(PostGroup postGroup, Category category) {
        return Condition.builder()
                .postGroup(postGroup)
                .category(category)
                .sort(null)
                .build();
    }

    public static Condition createCondition(Sort sort, Category category) {
        return Condition.builder()
                .sort(sort)
                .category(category)
                .build();
    }

    public static Condition createCondition(Sort sort, Category category, String keyword) {
        return Condition.builder()
                .sort(sort)
                .category(category)
                .keyword(keyword)
                .build();
    }

    private static String formatLocalDateTime(LocalDateTime localDateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return localDateTime.format(formatter);
    }
}
