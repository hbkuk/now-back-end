package com.now.core.post.inquiry.domain.constants;

import com.now.core.post.common.domain.constants.UpdateOption;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

/**
 * 문의 게시글의 상태를 나타내는 Enum
 */
@RequiredArgsConstructor
public enum InquiryStatus {
    COMPLETE("답변 완료"),
    INCOMPLETE("답변 미완료");

    private final String title;

    public String getCode() {
        return name();
    }

    /**
     * 전달받은 값으로부터 해당하는 InquiryStatus enum 생성
     *
     * @param value InquiryStatus name에 해당하는 값
     * @return 전달받은 값으로부터 해당하는 InquiryStatus enum
     */
    public static InquiryStatus from(String value) {
        return Arrays.stream(InquiryStatus.values())
                .filter(inquiryStatus -> inquiryStatus.name().equals(value))
                .findAny()
                .orElse(null);
    }
}

