package com.now.core.report.domain.mapper;

import com.now.core.report.domain.Report;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ReportMapper {

    /**
     * 레포트 등록
     *
     * @param report 등록할 레포트 정보
     */
    void saveReport(Report report);
}
