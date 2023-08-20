package com.now.core.authentication.presentation;

import com.now.common.exception.ErrorType;
import com.now.core.authentication.application.AuthenticationService;
import com.now.core.authentication.application.JwtTokenService;
import com.now.core.authentication.application.dto.Token;
import com.now.core.authentication.application.util.CookieUtil;
import com.now.core.authentication.exception.InvalidTokenException;
import com.now.core.member.application.MemberService;
import com.now.core.member.domain.Member;
import com.now.core.member.presentation.dto.MemberProfile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

/**
 * 회원 인증 관련 작업을 위한 컨트롤러
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthenticationController {

    private final MemberService memberService;
    private final JwtTokenService jwtTokenService;
    private final AuthenticationService authenticationService;

    /**
     * 회원 정보를 조회 후 로그인 처리하는 핸들러 메서드
     *
     * @param member   조회할 회원 정보
     * @param response 응답 객체
     * @return ResponseEntity 객체 (HTTP 응답)
     */
    @PostMapping("/api/sign-in")
    public ResponseEntity<MemberProfile> signIn(@RequestBody Member member, HttpServletResponse response) {
        Member authenticatedMember = memberService.validateCredentialsAndRetrieveMember(member);
        Token token = memberService.generateAuthToken(authenticatedMember);

        setTokenCookiesInResponse(response, token);
        return ResponseEntity.ok().body(MemberProfile.from(authenticatedMember));
    }

    /**
     * 전달받은 토큰을 블랙리스트 등록 및 만료 시간을 0으로 설정하는 로그아웃 핸들러 메서드
     *
     * @param response 응답 객체
     * @return ResponseEntity 객체 (HTTP 응답)
     */
    @PostMapping("/api/log-out")
    public ResponseEntity<Void> logout(HttpServletResponse response,
                       @CookieValue(value = JwtTokenService.ACCESS_TOKEN_KEY, required = true) String accessToken,
                       @CookieValue(value = JwtTokenService.REFRESH_TOKEN_KEY, required = true) String refreshToken) {
        authenticationService.logout(accessToken, refreshToken);

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
                           @CookieValue(value = JwtTokenService.ACCESS_TOKEN_KEY, required = true) String accessToken,
                           @CookieValue(value = JwtTokenService.REFRESH_TOKEN_KEY, required = true) String refreshToken) {
        jwtTokenService.validateForRefresh(accessToken, refreshToken);

        authenticationService.logout(accessToken, refreshToken);

        setTokenCookiesInResponse(response, jwtTokenService.refreshTokens(refreshToken));
        return ResponseEntity.ok().build();
    }

    /**
     * 토큰 확인 후 회원 정보를 전달하는 핸들러 메서드
     *
     * @return ResponseEntity 객체 (HTTP 응답)
     */
    @PostMapping("/api/member/me")
    public ResponseEntity<MemberProfile> me(
                        @CookieValue(value = JwtTokenService.ACCESS_TOKEN_KEY, required = false) String accessToken) {
        authenticationService.isAccessTokenBlacklisted(accessToken);

        String memberId = extractMemberIdFromToken(accessToken);
        return ResponseEntity.ok().body(MemberProfile.from(memberService.getMember(memberId)));
    }

    /**
     * HTTP 응답에 토큰 관련 쿠키를 설정
     *
     * @param response 응답에 쿠키를 추가할 HttpServletResponse 객체
     * @param token 액세스 토큰과 리프레시 토큰 정보를 담은 토큰 객체
     */
    private void setTokenCookiesInResponse(HttpServletResponse response, Token token) {
        response.addCookie(CookieUtil.createCookieWithHttpOnly(JwtTokenService.ACCESS_TOKEN_KEY,
                token.getAccessToken(), true));
        response.addCookie(CookieUtil.createCookieWithPathAndHttpOnly(JwtTokenService.REFRESH_TOKEN_KEY,
                token.getRefreshToken(), "/api/refresh", true));
        response.addCookie(CookieUtil.createCookieWithPathAndHttpOnly(JwtTokenService.REFRESH_TOKEN_KEY,
                token.getRefreshToken(), "/api/log-out", true));
    }

    /**
     * HTTP 응답에 토큰 관련 쿠키를 삭제
     *
     * @param response 응답에 쿠키를 삭제할 HttpServletResponse 객체
     */
    private void deleteTokenCookiesInResponse(HttpServletResponse response) {
        response.addCookie(CookieUtil.deleteCookie(JwtTokenService.ACCESS_TOKEN_KEY));
        response.addCookie(CookieUtil.deleteCookie(JwtTokenService.REFRESH_TOKEN_KEY));
    }

    /**
     * 액세스 토큰으로부터 회원 아이디를 추출 후 반환
     * 
     * @param accessToken 액세스 토큰
     * @return 회원 아이디
     */
    private String extractMemberIdFromToken(String accessToken) {
        String memberId = null;
        try {
            memberId = (String) jwtTokenService.getClaim(accessToken, "id");
        } catch (Exception e) {
            throw new InvalidTokenException(ErrorType.NOT_FOUND_TOKEN);
        }
        return memberId;
    }
}
