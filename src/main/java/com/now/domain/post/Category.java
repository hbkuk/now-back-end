package com.now.domain.post;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.now.code.EnumMapperType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 게시물 카테고리를 나타내는 enum
 */
@Getter
@RequiredArgsConstructor
public enum Category implements EnumMapperType {
    EVENT("이벤트"),
    NEWS("새소식"),
    LIFESTYLE("사는얘기"),
    COMMUNITY_STUDY("모임&스터디"),
    DAILY_LIFE("일상"),
    ARTWORK("작품"),
    SERVICE("서비스"),
    TECHNOLOGY("기술");

    @JsonCreator
    public static Category from(String value) {
        for (Category category : Category.values()) {
            if (category.getTitle().equals(value)) {
                return category;
            }
        }
        return null;
    }

    @JsonValue
    private final String title;

    @Override
    public String getCode() {
        return name();
    }

    @Override
    public String getTitle() {
        return title;
    }
}
