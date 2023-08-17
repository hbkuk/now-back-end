package com.now.config.fixtures.post.dto;

import com.now.core.category.domain.constants.Category;
import com.now.core.post.presentation.dto.Condition;
import com.now.core.post.presentation.dto.constants.Sort;

public class ConditionFixture {

    public static Condition createCondition(Category category) {
        return Condition.builder()
                .startDate("2020-08-08")
                .endDate("2023-08-08")
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
}
