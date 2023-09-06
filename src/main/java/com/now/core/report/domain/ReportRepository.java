package com.now.core.report.domain;

import com.now.core.report.domain.mapper.ReportMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/**
 * 레포트 관련 정보를 관리하는 레포지토리
 */
@Repository
@RequiredArgsConstructor
public class ReportRepository {

    private final ReportMapper reportMapper;

    /**
     * 레포트 등록
     *
     * @param report 등록할 레포트 정보
     */
    public void save(Report report) {
        reportMapper.saveReport(report);
    }
}
