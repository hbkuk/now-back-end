package com.now.core.member.application;

import com.now.common.exception.ErrorType;
import com.now.common.security.PasswordSecurityManager;
import com.now.core.authentication.application.JwtTokenProvider;
import com.now.core.authentication.application.dto.jwtTokens;
import com.now.core.authentication.application.dto.TokenClaims;
import com.now.core.authentication.constants.Authority;
import com.now.core.member.domain.Member;
import com.now.core.member.domain.MemberRepository;
import com.now.core.member.exception.DuplicateMemberInfoException;
import com.now.core.member.exception.InvalidMemberException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 회원 관련 비즈니스 로직을 처리하는 서비스
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordSecurityManager passwordSecurityManager;

    /**
     * 회원 정보를 등록하는 메서드
     *
     * @param member 등록할 회원 정보
     */
    public void registerMember(Member member) {
        duplicateMemberCheck(member);
        memberRepository.saveMember(member.updatePassword(passwordSecurityManager.encodeWithSalt(member.getPassword())));
    }

    /**
     * 회원 정보를 수정하는 메서드
     * 
     * @param member 수정할 회원 정보
     */
    public void updateMember(Member member) {
        boolean duplicateNickname = memberRepository.existsByNickname(member.getNickname());
        if (duplicateNickname) {
            throw new DuplicateMemberInfoException(ErrorType.DUPLICATE_MEMBER_INFO_ID_AND_NICKNAME);
        }

        memberRepository.updateMember(member);
    }

    /**
     * 회원의 자격 증명을 확인하고, 유효한 회원 정보를 반환
     *
     * @param member 자격 증명을 확인할 회원 객체
     * @return 유효한 회원 정보를 담고 있는 객체
     */
    public Member validateCredentialsAndRetrieveMember(Member member) {
        Member savedMember = getMember(member.getId());

        if (!passwordSecurityManager.matchesWithSalt(member.getPassword(), savedMember.getPassword())) {
            throw new InvalidMemberException(ErrorType.NOT_FOUND_MEMBER);
        }
        return savedMember;
    }

    /**
     * 회원의 정보를 기반으로 액세스 토큰과 리프레시 토큰 생성
     *
     * @param member 토큰을 생성할 회원 객체
     * @return 액세스 토큰과 리프레시 토큰을 담은 인증 토큰
     */
    public jwtTokens generateAuthToken(Member member) {
        return jwtTokens.builder()
                .accessToken(jwtTokenProvider.createAccessToken(generateTokenClaims(member)))
                .refreshToken(jwtTokenProvider.createRefreshToken(generateTokenClaims(member)))
                .build();
    }

    /**
     * 회원 아이디(authorId)에 해당하는 사용자를 조회 후 {@link Member}를 반환
     *
     * @param memberId 조회할 회원의 아이디
     * @return 회원을 조회 후 {@link Member}를 반환
     */
    public Member getMember(String memberId) {
        Member member = memberRepository.findById(memberId);
        if (member == null) {
            throw new InvalidMemberException(ErrorType.NOT_FOUND_MEMBER);
        }
        return member;
    }

    /**
     * 전달받은 회원 정보를 기반으로 클레임을 생성 후 반환
     *
     * @param member 회원 정보
     * @return 클레임을 생성 후 반환
     */
    private TokenClaims generateTokenClaims(Member member) { // TODO: Claims의 key값인 id, nickname, role에 대해서 강제할 방법
        return TokenClaims.create(
                Map.of("id", member.getId(),
                        "nickname", member.getNickname(),
                        "role", Authority.MEMBER.getValue()));
    }

    /**
     * 중복된 회원 정보를 체크
     *
     * @param member 체크할 사용자 정보
     */
    private void duplicateMemberCheck(Member member) {
        boolean duplicateId = memberRepository.existsById(member.getId());
        boolean duplicateNickname = memberRepository.existsByNickname(member.getNickname());

        if (duplicateId || duplicateNickname) {
            ErrorType errorType = duplicateId && duplicateNickname ? ErrorType.DUPLICATE_MEMBER_INFO_ID_AND_NICKNAME :
                    duplicateId ? ErrorType.DUPLICATE_MEMBER_INFO_ID : ErrorType.DUPLICATE_MEMBER_INFO_NICKNAME;
            throw new DuplicateMemberInfoException(errorType);
        }
    }
}
