package com.now.core.authentication.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final TokenBlackList tokenBlackList;

    /**
     * 로그아웃 처리
     *
     * @param accessToken  로그아웃 대상의 액세스 토큰
     * @param refreshToken 로그아웃 대상의 리프레시 토큰
     */
    public void revokeTokens(String accessToken, String refreshToken) {
        tokenBlackList.addToAccessTokenBlacklist(accessToken);
        tokenBlackList.addToRefreshTokenBlacklist(refreshToken);
    }
}
