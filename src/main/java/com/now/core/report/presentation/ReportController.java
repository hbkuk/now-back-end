package com.now.core.report.presentation;

import com.now.core.authentication.presentation.client.ClientPrincipal;
import com.now.core.report.application.ReportService;
import com.now.core.report.domain.Report;
import com.now.core.report.domain.constants.ReportType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * Report 관련 작업을 위한 컨트롤러
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    /**
     * 버그 제보
     *
     * @param ipAddress 클라이언트 IP
     * @param report    버그 정보
     * @return 생성된 댓글에 대한 CREATED 응답을 반환
     */
    @PostMapping("/api/report/bug")
    public ResponseEntity<Void> saveBug(@ClientPrincipal String ipAddress,
                                        @RequestBody @Valid Report report) {
        reportService.saveReport(report.updateType(ReportType.BUG).updateIpAddress(ipAddress));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 피드백 저장
     *
     * @param ipAddress 클라이언트 IP
     * @param report    버그 정보
     * @return 생성된 댓글에 대한 CREATED 응답을 반환
     */
    @PostMapping("/api/report/feedback")
    public ResponseEntity<Void> saveFeedback(@ClientPrincipal String ipAddress,
                                             @RequestBody @Valid Report report) {
        reportService.saveReport(report.updateType(ReportType.FEEDBACK).updateIpAddress(ipAddress));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
