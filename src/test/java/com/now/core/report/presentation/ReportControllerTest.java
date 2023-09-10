package com.now.core.report.presentation;

import com.now.config.document.utils.RestDocsTestSupport;
import com.now.core.report.domain.Report;
import com.now.core.report.domain.constants.ReportType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static com.now.config.fixtures.report.ReportFixture.createReportForSave;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;

@DisplayName("Report 컨트롤러는")
class ReportControllerTest extends RestDocsTestSupport {

    @Test
    @DisplayName("버그 제보")
    void saveBug() throws Exception {
        String ipAddress = "127.0.0.1";
        Report report = createReportForSave("버그 제보하겠습니다. 제가...");

        ResultActions resultActions =
                mockMvc.perform(RestDocumentationRequestBuilders.post("/api/report/bug")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(report)))
                        .andExpect(MockMvcResultMatchers.status().isCreated());

        resultActions
                .andDo(restDocs.document(
                        requestFields(
                                fieldWithPath("content").description("내용")
                        )
                ));

        verify(reportService, times(1)).saveReport(any(Report.class));
    }

    @Test
    @DisplayName("피드백 저장")
    void saveFeedback() throws Exception {
        String ipAddress = "127.0.0.1";
        Report report = createReportForSave("피드백은요...");

        ResultActions resultActions =
                mockMvc.perform(RestDocumentationRequestBuilders.post("/api/report/feedback")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(report)))
                        .andExpect(MockMvcResultMatchers.status().isCreated());

        resultActions
                .andDo(restDocs.document(
                        requestFields(
                                fieldWithPath("content").description("내용")
                        )
                ));

        verify(reportService, times(1)).saveReport(any(Report.class));
    }
}