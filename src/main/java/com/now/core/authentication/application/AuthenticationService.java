package com.now.core.authentication.application;

import com.now.common.exception.ErrorType;
import com.now.core.authentication.exception.InvalidTokenException;
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
    public void logout(String accessToken, String refreshToken) {
        tokenBlackList.addToAccessTokenBlacklist(accessToken);
        tokenBlackList.addToRefreshTokenBlacklist(refreshToken);
    }

    /**
     * 주어진 AccessToken이 블랙리스트에 해당하지 않는다면 InvalidTokenException throw
     *
     * @param accessToken 액세스 토큰
     */
    public void isAccessTokenBlacklisted(String accessToken) {
        if (tokenBlackList.isAccessTokenBlacklisted(accessToken)) {
            throw new InvalidTokenException(ErrorType.LOGGED_OUT_TOKEN);
        }
    }
}
