package com.now.core.report.presentation;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

/**
 * 현재 사용자의 ip 정보를 관리하는 컴포넌트
 * Request 범위 스코프, 요청마다 인스턴스가 생성 및 관리
 */
@Component
@RequestScope
public class SenderContext {

    private String ipAddress;

    /**
     * 현재 사용자의 ip 정보를 반환
     *
     * @return 현재 사용자의 주체(principal) 정보
     */
    public String getIpAddress() {
        return ipAddress;
    }

    /**
     * 현재 사용자의 ip 정보를 설정
     *
     * @param ipAddress 사용자의 ip 정보
     */
    public void setIpAddress(final String ipAddress) {
        this.ipAddress = ipAddress;
    }
}
