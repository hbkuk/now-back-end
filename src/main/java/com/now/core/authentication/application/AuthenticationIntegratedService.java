package com.now.core.authentication.application;

import com.now.core.authentication.application.dto.jwtTokens;
import com.now.core.member.application.MemberService;
import com.now.core.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationIntegratedService {

    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationService authenticationService;

    /**
     * 회원 정보를 검증하고 반환
     *
     * @param member 검증할 회원 정보
     * @return 검증된 회원 정보
     */
    public Member retrieveMember(Member member) {
        return memberService.validateCredentialsAndRetrieveMember(member);
    }

    /**
     * 회원에 대한 인증 토큰을 생성
     *
     * @param member 인증 토큰을 생성할 회원 정보
     * @return 생성된 인증 토큰
     */
    public jwtTokens generateAuthToken(Member member) {
        return memberService.generateAuthToken(member);
    }

    /**
     * 토큰 무효화
     *
     * @param accessToken  액세스 토큰
     * @param refreshToken 리프레시 토큰
     */
    public void revokeTokens(String accessToken, String refreshToken) {
        authenticationService.revokeTokens(accessToken, refreshToken);
    }

    /**
     * 토큰 검증 후 무효화
     *
     * @param accessToken  액세스 토큰
     * @param refreshToken 리프레시 토큰
     */
    public void validateAndRevokeTokens(String accessToken, String refreshToken) {
        jwtTokenProvider.validateForRefresh(accessToken, refreshToken);
        authenticationService.revokeTokens(accessToken, refreshToken);
    }

    /**
     * 리프레시 토큰을 사용하여 새로운 액세스 토큰 발급
     *
     * @param refreshToken 리프레시 토큰
     * @return 새로운 액세스 토큰
     */
    public jwtTokens refreshTokens(String refreshToken) {
        return jwtTokenProvider.refreshTokens(refreshToken);
    }

    /**
     * 액세스 토큰에서 회원 정보 추출
     *
     * @param accessToken 액세스 토큰
     * @return 추출된 회원 정보
     */
    public Member extractMemberFromToken(String accessToken) {
        return memberService.getMember((String) jwtTokenProvider.getClaim(accessToken, "id"));
    }
}
