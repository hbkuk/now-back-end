package com.now.core.authentication.presentation;

import com.now.core.authentication.application.AuthenticationIntegratedService;
import com.now.core.authentication.application.JwtTokenProvider;
import com.now.core.authentication.application.dto.Token;
import com.now.core.authentication.application.util.CookieUtil;
import com.now.core.member.domain.Member;
import com.now.core.member.presentation.dto.MemberProfile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
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
public class AuthenticationController {

    private final AuthenticationIntegratedService authenticationIntegratedService;

    /**
     * 회원 정보를 조회 후 토큰을 쿠키에 세팅 후 응답하는 핸들러 메서드
     *
     * @param member   조회할 회원 정보
     * @param response 응답 객체
     * @return ResponseEntity 객체 (HTTP 응답)
     */
    @PostMapping("/api/sign-in")
    public ResponseEntity<MemberProfile> signIn(@RequestBody Member member, HttpServletResponse response) {
        Member authenticatedMember = authenticationIntegratedService.retrieveMember(member);
        setTokenCookiesInResponse(response, authenticationIntegratedService.generateAuthToken(authenticatedMember));
        return ResponseEntity.ok().body(MemberProfile.from(authenticatedMember));
    }

    /**
     * 전달받은 토큰을 블랙리스트 등록 및 만료 시간을 0으로 설정하는 로그아웃 핸들러 메서드
     *
     * @param response 응답 객체
     * @return ResponseEntity 객체 (HTTP 응답)
     */
    @PostMapping("/api/log-out") // TODO: Required Test Code In Interceptor..
    public ResponseEntity<Void> logout(HttpServletResponse response,
                                       @CookieValue(value = JwtTokenProvider.ACCESS_TOKEN_KEY, required = true) String accessToken,
                                       @CookieValue(value = JwtTokenProvider.REFRESH_TOKEN_KEY, required = true) String refreshToken) {
        authenticationIntegratedService.revokeTokens(accessToken, refreshToken);
        deleteTokenCookiesInResponse(response);
        return ResponseEntity.ok().build();
    }

    /**
     * 토큰을 확인 후 Access및 Refresh 토큰을 재발급 처리하는 핸들러 메서드
     *
     * @param response    응답 객체
     * @param accessToken 액세스 토큰
     * @return ResponseEntity 객체 (HTTP 응답)
     */
    @PostMapping("/api/refresh")
    public ResponseEntity<HttpHeaders> refresh(HttpServletResponse response,
                                               @CookieValue(value = JwtTokenProvider.ACCESS_TOKEN_KEY, required = true) String accessToken,
                                               @CookieValue(value = JwtTokenProvider.REFRESH_TOKEN_KEY, required = true) String refreshToken) {
        authenticationIntegratedService.validateAndRevokeTokens(accessToken, refreshToken);
        setTokenCookiesInResponse(response, authenticationIntegratedService.refreshTokens(refreshToken));
        return ResponseEntity.ok().build();
    }

    /**
     * 토큰 확인 후 회원 정보를 전달하는 핸들러 메서드
     *
     * @return ResponseEntity 객체 (HTTP 응답)
     */
    @PostMapping("/api/member/me")
    public ResponseEntity<MemberProfile> me(
            @CookieValue(value = JwtTokenProvider.ACCESS_TOKEN_KEY, required = false) String accessToken) {
        return ResponseEntity.ok().body(MemberProfile.from(authenticationIntegratedService.extractMemberFromToken(accessToken)));
    }

    /**
     * HTTP 응답에 토큰 관련 쿠키를 설정
     *
     * @param response 응답에 쿠키를 추가할 HttpServletResponse 객체
     * @param token    액세스 토큰과 리프레시 토큰 정보를 담은 토큰 객체
     */
    private void setTokenCookiesInResponse(HttpServletResponse response, Token token) {
        response.setHeader(RESPONSE_COOKIE_NAME_IN_HEADERS, CookieUtil.createResponseCookieWithHttpOnly(JwtTokenProvider.ACCESS_TOKEN_KEY,
                token.getAccessToken(), true).toString());
        response.addHeader(RESPONSE_COOKIE_NAME_IN_HEADERS, CookieUtil.createResponseCookieWithPathAndHttpOnly(JwtTokenProvider.REFRESH_TOKEN_KEY,
                token.getRefreshToken(), "/api/refresh", true).toString());
        response.addHeader(RESPONSE_COOKIE_NAME_IN_HEADERS, CookieUtil.createResponseCookieWithPathAndHttpOnly(JwtTokenProvider.REFRESH_TOKEN_KEY,
                token.getRefreshToken(), "/api/log-out", true).toString());
    }

    /**
     * HTTP 응답에 토큰 관련 쿠키를 삭제
     *
     * @param response 응답에 쿠키를 삭제할 HttpServletResponse 객체
     */
    private void deleteTokenCookiesInResponse(HttpServletResponse response) {
        response.setHeader(RESPONSE_COOKIE_NAME_IN_HEADERS, CookieUtil.deleteResponseCookie(JwtTokenProvider.ACCESS_TOKEN_KEY).toString());
        response.addHeader(RESPONSE_COOKIE_NAME_IN_HEADERS, CookieUtil.deleteResponseCookie(JwtTokenProvider.REFRESH_TOKEN_KEY).toString());
    }
}
