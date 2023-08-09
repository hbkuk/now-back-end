package com.now.core.authentication.presentation;

import com.now.common.exception.ErrorType;
import com.now.core.authentication.exception.InvalidAuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

/**
 * 현재 사용자의 인증 상태를 관리하는 컴포넌트
 * Request 범위 스코프, 요청마다 인스턴스가 생성 및 관리
 */
@Component
@RequestScope
public class AuthenticationContext {

    private Long principal;

    /**
     * 현재 사용자의 주체(principal) 정보를 반환
     *
     * @return 현재 사용자의 주체(principal) 정보
     */
    public Long getPrincipal() {
        if (principal == null) {
            throw new InvalidAuthenticationException(ErrorType.NOT_AUTHENTICATED);
        }
        return principal;
    }

    /**
     * 현재 사용자의 주체(principal) 정보를 설정
     *
     * @param principal 사용자의 주체(principal) 정보
     */
    public void setPrincipal(final Long principal) {
        if (this.principal != null) {
            throw new InvalidAuthenticationException(ErrorType.ALREADY_AUTHENTICATED);
        }
        this.principal = principal;
    }
}
