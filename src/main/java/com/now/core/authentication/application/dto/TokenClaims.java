package com.now.core.authentication.application.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * JWT 토큰의 클레임 정보를 담고 있는 객체
 */
@Getter
@EqualsAndHashCode
public class TokenClaims {
    private final Map<String, Object> claims;

    /**
     * TokenClaims 객체를 생성
     *
     * @param claims 토큰의 클레임 정보를 담은 Map 객체
     */
    private TokenClaims(Map<String, Object> claims) {
        this.claims = new HashMap<>(claims);
    }

    /**
     * TokenClaims 객체를 생성하는 정적 팩토리 메서드
     *
     * @param claims 토큰의 클레임 정보를 담은 Map 객체
     * @return 생성된 TokenClaims 객체
     */
    public static TokenClaims create(Map<String, Object> claims) {
        return new TokenClaims(claims);
    }
}



