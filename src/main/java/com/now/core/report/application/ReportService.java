package com.now.core.report.application;

import com.now.core.report.domain.Report;
import com.now.core.report.domain.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 레포트 관련 비즈니스 로직을 처리하는 서비스
 */
@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;

    /**
     * 레포트 등록
     *
     * @param report 등록할 레포트 정보
     */
    public void saveReport(Report report) {
        reportRepository.save(report);
    }
}
