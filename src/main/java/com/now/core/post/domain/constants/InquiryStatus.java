package com.now.core.post.domain.constants;

import lombok.RequiredArgsConstructor;

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
        for (InquiryStatus inquiryStatus : InquiryStatus.values()) {
            if (inquiryStatus.name().equals(value)) {
                return inquiryStatus;
            }
        }
        return null;
    }
}

