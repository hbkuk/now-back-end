package com.now.core.admin.authentication.presentation;

import com.now.core.admin.authentication.application.ManagerAuthenticationService;
import com.now.core.admin.authentication.domain.Manager;
import com.now.core.admin.authentication.presentation.dto.ManagerProfile;
import com.now.core.authentication.application.JwtTokenProvider;
import com.now.core.authentication.application.dto.jwtTokens;
import com.now.core.authentication.application.util.CookieUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

import static com.now.core.authentication.application.util.CookieUtil.RESPONSE_COOKIE_NAME_IN_HEADERS;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ManagerAuthenticationController {

    private final ManagerAuthenticationService managerAuthenticationService;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 토큰 확인 후 매니저 정보를 전달하는 핸들러 메서드
     *
     * @return ResponseEntity 객체 (HTTP 응답)
     */
    @PostMapping("/api/manager/me")
    public ResponseEntity<ManagerProfile> me(@CookieValue(value = JwtTokenProvider.ACCESS_TOKEN_KEY, required = false) String accessToken) {
        return ResponseEntity.ok()
                .body(ManagerProfile.from(managerAuthenticationService.getManager((String) jwtTokenProvider.getClaim(accessToken, "id"))));
    }

    /**
     * 매니저 정보를 조회 후 토큰을 쿠키에 세팅 후 응답하는 핸들러 메서드
     *
     * @param manager 조회할 매니저 정보
     * @return ResponseEntity 객체 (HTTP 응답)
     */
    @PostMapping("/api/manager/sign-in")
    public ResponseEntity<ManagerProfile> signIn(@RequestBody Manager manager, HttpServletResponse response) {
        Manager authenticatedManager = managerAuthenticationService.retrieveManager(manager);
        setTokenCookiesInResponse(response, managerAuthenticationService.generateAuthToken(authenticatedManager));
        return ResponseEntity.ok().body(ManagerProfile.from(authenticatedManager));
    }

    /**
     * HTTP 응답에 토큰 관련 쿠키를 설정
     *
     * @param response 응답에 쿠키를 추가할 HttpServletResponse 객체
     * @param token    액세스 토큰과 리프레시 토큰 정보를 담은 토큰 객체
     */
    private void setTokenCookiesInResponse(HttpServletResponse response, jwtTokens token) {
        response.setHeader(RESPONSE_COOKIE_NAME_IN_HEADERS, CookieUtil.createResponseCookieWithHttpOnly(JwtTokenProvider.ACCESS_TOKEN_KEY,
                token.getAccessToken(), true).toString());
        response.addHeader(RESPONSE_COOKIE_NAME_IN_HEADERS, CookieUtil.createResponseCookieWithPathAndHttpOnly(JwtTokenProvider.REFRESH_TOKEN_KEY,
                token.getRefreshToken(), "/api/refresh", true).toString());
        response.addHeader(RESPONSE_COOKIE_NAME_IN_HEADERS, CookieUtil.createResponseCookieWithPathAndHttpOnly(JwtTokenProvider.REFRESH_TOKEN_KEY,
                token.getRefreshToken(), "/api/log-out", true).toString());
    }
}
