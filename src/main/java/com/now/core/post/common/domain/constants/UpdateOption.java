package com.now.core.post.common.domain.constants;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 사진 게시글 수정 타입을 나타내는 enum
 */
@Getter
@AllArgsConstructor
public enum UpdateOption {
    EDIT_EXISTING("editExisting"),
    ADD_NEW("addNew");

    private final String optionValue;

    /**
     * 전달받은 값으로부터 해당하는 UpdateOption enum 생성
     *
     * @param value Category의 제목에 해당하는 값
     * @return 전달받은 값으로부터 해당하는 Category enum
     */
    @JsonCreator //  JSON 값을 열거형으로 변환할 때 사용
    public static UpdateOption from(String value) {
        return Arrays.stream(UpdateOption.values())
                .filter(updateOption -> updateOption.name().equals(value))
                .findAny()
                .orElse(null);
    }
}

