package com.now.core.authentication.config;

import com.now.common.exception.ErrorType;
import com.now.core.authentication.application.JwtTokenService;
import com.now.core.authentication.application.util.CookieUtil;
import com.now.core.authentication.constants.Authority;
import com.now.core.authentication.exception.InvalidAuthenticationException;
import com.now.core.authentication.exception.InvalidTokenException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 매니저 권한 인터셉터
 *
 * HandlerInterceptor 인터페이스를 구현하여 매니저 권한을 검사하는 인터셉터
 * JWT 토큰 서비스를 사용하여 토큰에서 권한 값을 추출하고, 매니저인지 확인
 * 매니저 권한이 아닌 경우에는 예외를 던지고 처리
 * HttpServletRequest의 속성에 사용자 ID와 권한을 설정합니다.
 */
@Component
@RequiredArgsConstructor
public class ManagerInterceptor implements HandlerInterceptor {

    private final JwtTokenService jwtTokenService;

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
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            throw new InvalidAuthenticationException(ErrorType.NOT_AUTHENTICATED);
        }

        String accessToken = CookieUtil.getValue(cookies, JwtTokenService.ACCESS_TOKEN_KEY);
        if (accessToken == null) {
            throw new InvalidAuthenticationException(ErrorType.NOT_AUTHENTICATED);
        }
        if (jwtTokenService.isTokenExpired(accessToken)) {
            throw new InvalidTokenException(ErrorType.EXPIRED_TOKEN);
        }

        Authority authority =
                Authority.valueOf((String) jwtTokenService.getClaim(accessToken, "role"));
        if(!Authority.isManager(authority)) {
            throw new InvalidAuthenticationException(ErrorType.FORBIDDEN);
        }

        request.setAttribute("id", jwtTokenService.getClaim(request.getHeader("Authorization"), "id"));
        request.setAttribute("role", jwtTokenService.getClaim(request.getHeader("Authorization"), "role"));
        return true;
    }
}

