package com.now.core.authentication.config;

import com.now.common.exception.ErrorType;
import com.now.core.authentication.application.JwtTokenProvider;
import com.now.core.authentication.application.TokenBlackList;
import com.now.core.authentication.exception.InvalidAuthenticationException;
import com.now.core.authentication.exception.InvalidTokenException;
import com.now.core.authentication.presentation.AuthenticationContext;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Arrays;

import static com.now.core.authentication.application.JwtTokenProvider.ACCESS_TOKEN_KEY;
import static com.now.core.authentication.application.JwtTokenProvider.BEARER_PREFIX;
import static com.now.core.authentication.application.util.CookieUtil.REQUEST_COOKIE_NAME_IN_HEADER;

/**
 * JWT 토큰을 검증하고 인증된 사용자의 주체 정보를 설정하는 인터셉터
 */
@Component
@RequiredArgsConstructor
public class AuthenticationInterceptor implements HandlerInterceptor {

    private final JwtTokenProvider jwtTokenProvider;
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
        if (shouldSkipAuthentication(request.getMethod())) {
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
        return (String) jwtTokenProvider.getClaim(accessToken, "id");
    }

    /**
     * 요청에서 액세스 토큰을 추출하고 검증
     *
     * @param request 현재 요청 객체
     * @return 추출 및 검증된 액세스 토큰
     */
    private String extractAccessTokenFromRequest(HttpServletRequest request) {
        String cookieHeader = request.getHeader(REQUEST_COOKIE_NAME_IN_HEADER);
        if (cookieHeader == null) {
            throw new InvalidAuthenticationException(ErrorType.NOT_AUTHENTICATED);
        }

        String accessToken = Arrays.stream(cookieHeader.split("; "))
                .filter(cookieItem -> cookieItem.startsWith(ACCESS_TOKEN_KEY))
                .map(cookieItem -> cookieItem.split("="))
                .filter(keyValue -> keyValue.length == 2 && keyValue[0].equals(ACCESS_TOKEN_KEY))
                .map(keyValue -> keyValue[1].trim())
                .filter(cookieValue -> cookieValue.startsWith(BEARER_PREFIX))
                .findFirst()
                .orElse(null);

        if (accessToken == null) {
            throw new InvalidAuthenticationException(ErrorType.NOT_AUTHENTICATED);
        }
        if (jwtTokenProvider.isTokenExpired(accessToken)) {
            throw new InvalidTokenException(ErrorType.EXPIRED_ACCESS_TOKEN);
        }
        if (tokenBlacklist.isAccessTokenBlacklisted(accessToken)) {
            throw new InvalidTokenException(ErrorType.LOGGED_OUT_TOKEN);
        }
        return accessToken;
    }

    /**
     * 인증을 Skip 가능하다면 true 반환, 그렇지 않다면 false 반환(GET 및 OPTIONS 요청인 경우 true)
     *
     * @param httpMethod HTTP 요청 메서드
     * @return 인증을 Skip 가능하다면 true 반환, 그렇지 않다면 false 반환(GET 및 OPTIONS 요청인 경우 true)
     */
    private boolean shouldSkipAuthentication(String httpMethod) {
        return isGetMethod(httpMethod) || isOptionMethod(httpMethod);
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

    /**
     * HTTP 요청 메서드가 OPTIONS인지 확인
     *
     * @param httpMethod 확인할 HTTP 요청 메서드
     * @return HTTP 요청 메서드가 OPTIONS인 경우 true, 그렇지 않은 경우 false
     */
    private boolean isOptionMethod(String httpMethod) {
        return HttpMethod.valueOf(httpMethod) == HttpMethod.OPTIONS;
    }
}

