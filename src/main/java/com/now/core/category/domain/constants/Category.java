package com.now.core.category.domain.constants;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 게시물 카테고리를 나타내는 enum
 */
@Getter
@RequiredArgsConstructor
public enum Category {
    EVENT("이벤트"),
    NEWS("새소식"),

    LIFESTYLE("사는얘기"),
    COMMUNITY_STUDY("모임&스터디"),

    DAILY_LIFE("일상"),
    ARTWORK("작품"),

    SERVICE("서비스"),
    TECHNOLOGY("기술");

    private final String title;

    /**
     * enum 상수의 코드 값을 반환
     *
     * @return 코드 값
     */
    @JsonValue  // 열거형의 특정 필드를 JSON 값으로 변환할 때 사용
    public String getCode() {
        return name();
    }

    /**
     * enum 상수의 제목을 반환
     *
     * @return 제목
     */
    public String getTitle() {
        return title;
    }

    /**
     * 전달받은 값으로부터 해당하는 Category enum 생성
     *
     * @param value Category의 제목에 해당하는 값
     * @return 전달받은 값으로부터 해당하는 Category enum
     */
    @JsonCreator //  JSON 값을 열거형으로 변환할 때 사용
    public static Category from(String value) {
        for (Category category : Category.values()) {
            if (category.name().equals(value)) {
                return category;
            }
        }
        return null;
    }
}
