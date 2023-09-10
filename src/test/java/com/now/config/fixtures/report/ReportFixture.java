package com.now.config.fixtures.report;

import com.now.core.report.domain.Report;

public class ReportFixture {
    public static Report createReportForSave(String content) {
        return Report.builder()
                .content(content)
                .build();
    }
}
