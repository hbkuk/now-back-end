package com.now.core.authentication.presentation;

import com.now.core.authentication.application.AuthenticationService;
import com.now.core.authentication.application.JwtTokenService;
import com.now.core.authentication.application.dto.Token;
import com.now.core.authentication.application.util.CookieUtil;
import com.now.core.member.application.MemberService;
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
        log.debug("signIn 핸들러 메서드 호출, Member : {}", member);

        Member authenticatedMember = memberService.validateCredentialsAndRetrieveMember(member);
        Token token = memberService.generateAuthToken(authenticatedMember);

        response.addCookie(CookieUtil.generateHttpOnlyCookie(JwtTokenService.ACCESS_TOKEN_KEY, token.getAccessToken(), true));
        response.addCookie(CookieUtil.generateHttpOnlyCookie(JwtTokenService.REFRESH_TOKEN_KEY, token.getRefreshToken(), true));

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
        log.debug("logout 핸들러 메서드 호출");

        authenticationService.logout(accessToken, refreshToken);

        response.addCookie(CookieUtil.deleteCookie(JwtTokenService.ACCESS_TOKEN_KEY));
        response.addCookie(CookieUtil.deleteCookie(JwtTokenService.REFRESH_TOKEN_KEY));

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
        log.debug("refresh 핸들러 메서드 호출");

        jwtTokenService.validateForRefresh(accessToken, refreshToken);

        authenticationService.logout(accessToken, refreshToken);

        Token newTokens = jwtTokenService.refreshTokens(refreshToken);
        response.addCookie(CookieUtil.generateHttpOnlyCookie(JwtTokenService.ACCESS_TOKEN_KEY, newTokens.getAccessToken(), true));
        response.addCookie(CookieUtil.generateHttpOnlyCookie(JwtTokenService.REFRESH_TOKEN_KEY, newTokens.getRefreshToken(), true));

        return ResponseEntity.ok().build();
    }
}
