package com.now.core.post.inquiry.domain.constants;

import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor
public enum PrivacyUpdateOption {
    TO_PUBLIC("공개글 설정"),
    TO_PRIVATE("비공개글로 변경"),
    CHANGE_PASSWORD("비밀번호 변경"),
    KEEP_PASSWORD("비밀번호 유지");

    private final String title;

    /**
     * 전달받은 값으로부터 해당하는 PrivacyUpdateOption enum 생성
     *
     * @param value PrivacyUpdateOption name에 해당하는 값
     * @return 전달받은 값으로부터 해당하는 PrivacyUpdateOption enum
     */
    public static PrivacyUpdateOption from(String value) {
        return Arrays.stream(PrivacyUpdateOption.values())
                .filter(privacyUpdateOption -> privacyUpdateOption.name().equals(value))
                .findAny()
                .orElse(null);
    }

    public String getCode() {
        return name();
    }

    /**
     * 전달받은 privacy 정보를 기반으로 수정이 가능하다면 true 반환, 그렇지 않다면 false 반환
     *
     * @param isPrivacy 비공개 여부
     * @return 수정이 가능하다면 true 반환, 그렇지 않다면 false 반환
     */
    public boolean canUpdate(Boolean isPrivacy) {
        if (isPrivacy) {
            return (this == TO_PUBLIC) || (this == CHANGE_PASSWORD) || (this == KEEP_PASSWORD);
        }
        if (!isPrivacy) {
            return (this == TO_PRIVATE) || (this == TO_PUBLIC);
        }
        return false;
    }
}
