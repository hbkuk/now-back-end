package com.now.core.post.presentation.dto.constants;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;


/**
 * 정렬 방식을 나타내는 Enum
 */
// TODO: 카테고리 방식과 동일하게 프론트로 전달 고려, 추후 정렬 조건 추가
//    COMMENTED("commented"),
//    SCRAPED("scraped");
@Getter
@AllArgsConstructor
public enum Sort {
    LATEST("latest"),
    RECOMMENDED("recommended"),
    MOST_VIEWED("most_viewed");

    private final String key;

    /**
     * 전달받은 값으로부터 해당하는 Sort enum 생성
     *
     * @param value Sort 이름에 해당하는 값
     * @return 전달받은 값으로부터 해당하는 Sort enum
     */
    @JsonCreator //  JSON 값을 열거형으로 변환할 때 사용
    public static Sort from(String value) {
        for (Sort sort : Sort.values()) {
            if (sort.name().equals(value)) {
                return sort;
            }
        }
        return null;
    }
}