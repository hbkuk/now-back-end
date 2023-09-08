package com.now.core.authentication.presentation.client;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

/**
 * 현재 클라이언트의 주체 정보를 관리하는 컴포넌트
 * Request 범위 스코프, 요청마다 인스턴스가 생성 및 관리
 */
@Component
@RequestScope
public class ClientContext {

    private String principal;

    /**
     * 현재 클라이언트의 주체(principal) 정보를 설정
     *
     * @return 현재 클라이언트의 주체(principal) 정보
     */
    public String getPrincipal() {
        return principal;
    }

    /**
     * 현재 클라이언트의 주체(principal) 정보를 설정
     *
     * @param principal 클라이언트의 주체 정보
     */
    public void setPrincipal(final String principal) {
        this.principal = principal;
    }
}
