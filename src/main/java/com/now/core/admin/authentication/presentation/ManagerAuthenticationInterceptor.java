package com.now.core.admin.authentication.presentation;

import com.now.common.exception.ErrorType;
import com.now.core.authentication.application.JwtTokenProvider;
import com.now.core.authentication.application.TokenBlackList;
import com.now.core.authentication.application.util.CookieUtil;
import com.now.core.authentication.constants.Authority;
import com.now.core.authentication.exception.InvalidAuthenticationException;
import com.now.core.authentication.exception.InvalidTokenException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

import static com.now.core.authentication.application.JwtTokenProvider.ACCESS_TOKEN_KEY;
import static com.now.core.authentication.application.JwtTokenProvider.BEARER_PREFIX;
import static com.now.core.authentication.application.util.CookieUtil.REQUEST_COOKIE_NAME_IN_HEADER;

@Component
@RequiredArgsConstructor
public class ManagerAuthenticationInterceptor implements HandlerInterceptor {

    private final JwtTokenProvider jwtTokenProvider;
    private final TokenBlackList tokenBlacklist;

    /**
     * 요청 처리 전에 실행되는 메서드
     *
     * @param request  현재 요청을 나타내는 HttpServletRequest 객체
     * @param response 현재 응답을 나타내는 HttpServletResponse 객체
     * @param handler  현재 요청을 처리할 핸들러 객체
     * @return 요청 처리 여부를 나타내는 boolean 값
     * @throws InvalidAuthenticationException 매니저 권한이 아닌 경우 발생하는 예외
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (isOptionMethod(request.getMethod())) {
            return true;
        }

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
        if(Authority.valueOf((String) jwtTokenProvider.getClaim(accessToken, "role")) != Authority.MANAGER) {
            throw new InvalidAuthenticationException(ErrorType.FORBIDDEN);
        }
        return true;
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