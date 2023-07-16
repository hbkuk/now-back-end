package com.now.core.authentication.config;

import com.now.core.authentication.application.JwtTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * HandlerInterceptor 인터페이스를 구현하여 JWT 토큰의 처리 담당
 * 인증된 유저 토큰에서 클레임 값을 추출 후 HttpServletRequest의 속성으로 설정
 */
@Component
@RequiredArgsConstructor
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtTokenService jwtTokenService;

    /**
     * 요청 처리 전에 실행되는 메서드
     * 토큰의 클레임 값을 추출하여 HttpServletRequest의 속성으로 설정
     *
     * @param request  현재 요청을 나타내는 HttpServletRequest 객체
     * @param response 현재 응답을 나타내는 HttpServletResponse 객체
     * @param handler  현재 요청을 처리할 핸들러 객체
     * @return 요청 처리 여부를 나타내는 boolean 값
     * @throws Exception 예외가 발생한 경우
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if(isGetMethod(request.getMethod())) {
            return true;
        }

        request.setAttribute("id", jwtTokenService.getClaim(request.getHeader("Authorization"), "id"));
        request.setAttribute("role", jwtTokenService.getClaim(request.getHeader("Authorization"), "role"));
        return true;
    }

    /**
     * 전달받은 HTTP 메서드가 GET 메서드라면 ture 반환, 그렇지 않다면 false 반환
     *
     * @param httpMethod 확인할 HTTP 메서드
     * @return 전달받은 HTTP 메서드가 GET 메서드라면 ture 반환, 그렇지 않다면 false 반환
     */
    private boolean isGetMethod(String httpMethod) {
        return HttpMethod.valueOf(httpMethod) == HttpMethod.GET;
    }
}
