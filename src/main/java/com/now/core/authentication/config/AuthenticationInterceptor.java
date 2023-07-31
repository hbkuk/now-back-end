package com.now.core.authentication.config;

import com.now.common.exception.ErrorType;
import com.now.core.authentication.application.JwtTokenService;
import com.now.core.authentication.application.TokenBlackList;
import com.now.core.authentication.application.util.CookieUtil;
import com.now.core.authentication.exception.InvalidAuthenticationException;
import com.now.core.authentication.exception.InvalidTokenException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * HandlerInterceptor 인터페이스를 구현하여 JWT 토큰의 처리 담당
 * 인증된 유저 토큰에서 클레임 값을 추출 후 HttpServletRequest의 속성으로 설정
 */
@Component
@RequiredArgsConstructor
public class AuthenticationInterceptor implements HandlerInterceptor {

    private final JwtTokenService jwtTokenService;
    private final TokenBlackList tokenBlacklist;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (isGetMethod(request.getMethod())) {
            return true;
        }

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

        request.setAttribute("id", jwtTokenService.getClaim(accessToken, "id"));
        request.setAttribute("role", jwtTokenService.getClaim(accessToken, "role"));
        return true;
    }

    private boolean isGetMethod(String httpMethod) {
        return HttpMethod.valueOf(httpMethod) == HttpMethod.GET;
    }
}

