package com.now.domain.post;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.now.core.EnumMapperType;
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

    /**
     * 전달받은 값으로부터 해당하는 Category enum 생성
     *
     * @param value Category의 제목에 해당하는 값
     * @return 전달받은 값으로부터 해당하는 Category enum
     */
    @JsonCreator //  JSON 값을 열거형으로 변환할 때 사용
    public static Category from(String value) {
        for (Category category : Category.values()) {
            if (category.getTitle().equals(value)) {
                return category;
            }
        }
        return null;
    }

    @JsonValue  // 열거형의 특정 필드를 JSON 값으로 변환할 때 사용
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
