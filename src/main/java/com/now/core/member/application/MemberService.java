package com.now.core.member.application;

import com.now.common.exception.ErrorType;
import com.now.common.security.PasswordSecurityManager;
import com.now.core.authentication.application.JwtTokenService;
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
    private final JwtTokenService jwtTokenManager;
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
     * 회원의 인증을 처리하고 JWT 토큰을 생성하여 반환
     *
     * @param member 로그인할 회원 정보
     * @return 인증된 회원에게 부여된 JWT(Json Web Token)
     * @throws InvalidMemberException 인증에 실패한 경우 발생하는 예외
     */
    public String generateAuthToken(Member member) {
        Member savedMember = memberRepository.findById(member.getId());

        if (savedMember == null) {
            throw new InvalidMemberException(ErrorType.NOT_FOUND_MEMBER);
        }

        if (!passwordSecurityManager.matchesWithSalt(member.getPassword(), savedMember.getPassword())) {
            throw new InvalidMemberException(ErrorType.NOT_FOUND_MEMBER);
        }

        return jwtTokenManager.create(TokenClaims.create(Map.of(
                "id", member.getId(), "role", Authority.MEMBER.getValue())));
    }

    /**
     * 회원 아이디(authorId)에 해당하는 사용자를 조회 후 {@link Member}를 반환
     *
     * @param memberId 조회할 회원의 아이디
     * @return 회원을 조회 후 {@link Member}를 반환
     */
    public Member findMemberById(String memberId) {
        Member member = memberRepository.findById(memberId);
        if(member == null) {
            throw new InvalidMemberException(ErrorType.NOT_FOUND_MEMBER);
        }
        return member;
    }

    /**
     * 중복된 회원 정보를 체크
     *
     * @param member 체크할 사용자 정보
     */
    public void duplicateMemberCheck(Member member) {
        boolean duplicateId = memberRepository.existsById(member.getId());
        boolean duplicateNickname = memberRepository.existsByNickname(member.getNickname());

        if (duplicateId || duplicateNickname) {
            ErrorType errorType = duplicateId && duplicateNickname ? ErrorType.DUPLICATE_MEMBER_INFO_ID_AND_NICKNAME :
                    duplicateId ? ErrorType.DUPLICATE_MEMBER_INFO_ID : ErrorType.DUPLICATE_MEMBER_INFO_NICKNAME;
            throw new DuplicateMemberInfoException(errorType);
        }
    }

}
