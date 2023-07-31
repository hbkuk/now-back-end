package com.now.core.authentication.application;

import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * 토큰 블랙리스트를 관리하는 클래스
 * 블랙리스트에 추가된 토큰은 유효하지 않은 토큰으로 간주됩
 */
// TODO: 추후 데이터베이스 테이블을 생성하여 블랙리스트를 관리
@Component
public class TokenBlackList {
    private final Set<String> accessTokenBlacklist = new HashSet<>();
    private final Set<String> refreshTokenBlacklist = new HashSet<>();

    /**
     * AccessToken을 블랙리스트에 추가
     *
     * @param accessToken 블랙리스트에 추가할 AccessToken
     */
    public void addToAccessTokenBlacklist(String accessToken) {
        accessTokenBlacklist.add(accessToken);
    }

    /**
     * RefreshToken을 블랙리스트에 추가
     *
     * @param refreshToken 블랙리스트에 추가할 RefreshToken
     */
    public void addToRefreshTokenBlacklist(String refreshToken) {
        refreshTokenBlacklist.add(refreshToken);
    }

    /**
     * 주어진 AccessToken이 블랙리스트에 해당 AccessToken이 있으면 true, 그렇지 않으면 false를 반환
     *
     * @param accessToken 확인할 AccessToken
     * @return 블랙리스트에 해당 AccessToken이 있으면 true, 그렇지 않으면 false를 반환
     */
    public boolean isAccessTokenBlacklisted(String accessToken) {
        return accessTokenBlacklist.contains(accessToken);
    }

    /**
     * 주어진 RefreshToken이 블랙리스트에 해당 RefreshToken이 있으면 true, 그렇지 않으면 false를 반환
     *
     * @param refreshToken 확인할 RefreshToken
     * @return 블랙리스트에 해당 RefreshToken이 있으면 true, 그렇지 않으면 false를 반환
     */
    public boolean isRefreshTokenBlacklisted(String refreshToken) {
        return refreshTokenBlacklist.contains(refreshToken);
    }
}
