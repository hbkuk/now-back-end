package com.now.core.report.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.now.core.report.domain.constants.ReportType;
import lombok.*;

import javax.validation.constraints.Size;

@Builder(toBuilder = true)
@Getter
@ToString(callSuper = true)
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class Report {

    @JsonIgnore
    private final Long reportIdx;

    @JsonIgnore
    private String ipAddress;

    @Size(min = 1, max = 2000, message = "{report.content.size}")
    private final String content;

    @JsonIgnore
    private ReportType reportType;

    public Report updateType(ReportType type) {
        this.reportType = type;
        return this;
    }

    public Report updateIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
        return this;
    }
}
