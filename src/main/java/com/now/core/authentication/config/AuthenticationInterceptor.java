package com.now.core.authentication.config;

import com.now.common.exception.ErrorType;
import com.now.core.authentication.application.JwtTokenService;
import com.now.core.authentication.application.TokenBlackList;
import com.now.core.authentication.application.util.CookieUtil;
import com.now.core.authentication.exception.InvalidAuthenticationException;
import com.now.core.authentication.exception.InvalidTokenException;
import com.now.core.authentication.presentation.AuthenticationContext;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * JWT 토큰을 검증하고 인증된 사용자의 주체 정보를 설정하는 인터셉터
 */
@Component
@RequiredArgsConstructor
public class AuthenticationInterceptor implements HandlerInterceptor {

    private final JwtTokenService jwtTokenService;
    private final TokenBlackList tokenBlacklist;
    private final AuthenticationContext authenticationContext;

    /**
     * 요청 처리 전에 토큰을 검증하고 인증된 사용자의 주체 정보를 설정
     *
     * @param request  현재 요청 객체
     * @param response 현재 응답 객체
     * @param handler  처리할 핸들러 객체
     * @return 요청 처리를 계속 진행할지 여부
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (isGetMethod(request.getMethod())) {
            return true;
        }

        String extractedAccessToken = extractAccessTokenFromRequest(request);
        authenticationContext.setPrincipal(getMemberIdFromToken(extractedAccessToken));
        return true;
    }

    /**
     * 토큰에서 회원 ID 정보를 추출
     *
     * @param accessToken 추출할 토큰
     * @return 토큰에서 추출한 회원 ID 정보
     */
    private String getMemberIdFromToken(String accessToken) {
        return (String) jwtTokenService.getClaim(accessToken, "id");
    }

    /**
     * 요청에서 액세스 토큰을 추출하고 검증
     *
     * @param request 현재 요청 객체
     * @return 추출 및 검증된 액세스 토큰
     */
    private String extractAccessTokenFromRequest(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            throw new InvalidAuthenticationException(ErrorType.NOT_AUTHENTICATED);
        }

        String accessToken = CookieUtil.getValue(cookies, JwtTokenService.ACCESS_TOKEN_KEY);
        if (accessToken == null) {
            throw new InvalidAuthenticationException(ErrorType.NOT_AUTHENTICATED);
        }
        if (jwtTokenService.isTokenExpired(accessToken)) {
            throw new InvalidTokenException(ErrorType.EXPIRED_ACCESS_TOKEN);
        }
        if (tokenBlacklist.isAccessTokenBlacklisted(accessToken)) {
            throw new InvalidTokenException(ErrorType.LOGGED_OUT_TOKEN);
        }
        String refreshToken = CookieUtil.getValue(cookies, JwtTokenService.REFRESH_TOKEN_KEY);
        if (tokenBlacklist.isRefreshTokenBlacklisted(refreshToken)) {
            throw new InvalidTokenException(ErrorType.LOGGED_OUT_TOKEN);
        }
        return accessToken;
    }

    /**
     * HTTP 요청 메서드가 GET인지 확인
     *
     * @param httpMethod 확인할 HTTP 요청 메서드
     * @return HTTP 요청 메서드가 GET인 경우 true, 그렇지 않은 경우 false
     */
    private boolean isGetMethod(String httpMethod) {
        return HttpMethod.valueOf(httpMethod) == HttpMethod.GET;
    }
}

