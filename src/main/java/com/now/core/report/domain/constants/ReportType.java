package com.now.core.report.domain.constants;

import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor
public enum ReportType {
    BUG("bug"),
    FEEDBACK("feedback");

    private final String title;

    public String getCode() {
        return name();
    }

    /**
     * 전달받은 값으로부터 해당하는 ReportType enum 생성
     *
     * @param value ReportType name에 해당하는 값
     * @return 전달받은 값으로부터 해당하는 ReportType enum
     */
    public static ReportType from(String value) {
        return Arrays.stream(ReportType.values())
                .filter(reportType -> reportType.name().equals(value))
                .findAny()
                .orElse(null);
    }
}
