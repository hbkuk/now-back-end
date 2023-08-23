package com.now.core.post.common.presentation.dto.constants;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;


/**
 * 반응을 나타내는 Enum
 */
@Getter
@AllArgsConstructor
public enum Reaction {
    NOTTING("notting"),

    LIKE("like"),
    UNLIKE("unlike"),

    DISLIKE("dislike"),
    UNDISLIKE("undislike");

    private final String value;

    /**
     * 전달받은 값으로부터 해당하는 Reaction enum 생성
     *
     * @param value Sort 이름에 해당하는 값
     * @return 전달받은 값으로부터 해당하는 Sort enum
     */
    @JsonCreator //  JSON 값을 열거형으로 변환할 때 사용
    public static Reaction from(String value) {
        for (Reaction reaction : Reaction.values()) {
            if (reaction.name().equals(value)) {
                return reaction;
            }
        }
        return null;
    }

    /**
     * 전달받은 객체가 현재 객체와 같다면 true 반환, 그렇지 않다면 false 반환
     *
     * @param reaction Reaction 객체
     * @return 전달받은 객체가 현재 객체와 같다면 true 반환, 그렇지 않다면 false 반환
     */
    public boolean isSameReaction(Reaction reaction) {
        return this == reaction;
    }

    /**
     * 현재 객체로 저장이 가능하다면 true, 그렇지 않다면 false 반환
     *
     * @return 현재 객체로 저장이 가능하다면 true, 그렇지 않다면 false 반환
     */
    public boolean canSave() {
        return (this == NOTTING) || (this == LIKE) || (this == DISLIKE);
    }

    /**
     * 전달받은 객체와 현재 객체를 비교해서 수정이 가능하다면 true, 그렇지 않다면 false 반환
     *
     * @param reaction Reaction 객체
     * @return 전달받은 객체와 현재 객체를 비교해서 수정이 가능하다면 true, 그렇지 않다면 false 반환
     */
    public boolean canUpdate(Reaction reaction) {
        if (isSameReaction(reaction)) {
            return false;
        }

        if (this == NOTTING) {
            return reaction == LIKE || reaction == DISLIKE;
        }

        if (this == LIKE) {
            return reaction == UNLIKE || reaction == DISLIKE;
        }

        if (this == DISLIKE) {
            return reaction == LIKE || reaction == UNDISLIKE;
        }

        if (this == UNLIKE) {
            return reaction == LIKE || reaction == DISLIKE;
        }

        if (this == UNDISLIKE) {
            return reaction == LIKE || reaction == DISLIKE;
        }

        return false;
    }
}