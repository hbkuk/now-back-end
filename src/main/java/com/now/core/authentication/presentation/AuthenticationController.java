package com.now.core.authentication.presentation;

import com.now.core.authentication.application.JwtTokenService;
import com.now.core.authentication.application.dto.Token;
import com.now.core.member.application.MemberService;
import com.now.core.member.domain.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

/**
 * 회원 인증 관련 작업을 위한 컨트롤러
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthenticationController {

    private final MemberService memberService;
    private final JwtTokenService jwtTokenService;

    /**
     * 회원 정보를 조회 후 로그인 처리하는 핸들러 메서드
     *
     * @param member 조회할 회원 정보
     * @return ResponseEntity 객체 (HTTP 응답)
     */
    @PostMapping("/api/sign-in")
    public ResponseEntity<HttpHeaders> signIn(@RequestBody Member member) {
        log.debug("signIn 핸들러 메서드 호출, Member : {}", member);

        Token token = memberService.generateAuthToken(member);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtTokenService.ACCESS_TOKEN_HEADER_KEY, token.getAccessToken());
        httpHeaders.add(JwtTokenService.REFRESH_TOKEN_HEADER_KEY, token.getRefreshToken());

        return ResponseEntity.ok().headers(httpHeaders).build();
    }

    /**
     * 토큰을 확인 후 AccessToken을 재발급 처리하는 핸들러 메서드
     *
     * @param accessToken AccessToken
     * @param refreshToken RefreshToken
     * @return ResponseEntity 객체 (HTTP 응답)
     */
    @PostMapping("/api/refresh")
    public ResponseEntity<HttpHeaders> refresh(@RequestHeader(name = HttpHeaders.AUTHORIZATION, required = false) String accessToken,
                                               @RequestHeader(name = JwtTokenService.REFRESH_TOKEN_HEADER_KEY, required = false) String refreshToken) {
        log.debug("refresh 핸들러 메서드 호출");

        jwtTokenService.validateTokensForRefresh(accessToken, refreshToken);

        Token newTokens = jwtTokenService.refreshTokens(refreshToken);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtTokenService.ACCESS_TOKEN_HEADER_KEY, newTokens.getAccessToken());
        httpHeaders.add(JwtTokenService.REFRESH_TOKEN_HEADER_KEY, newTokens.getRefreshToken());

        return ResponseEntity.ok().headers(httpHeaders).build();
    }
}
