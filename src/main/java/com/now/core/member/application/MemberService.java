package com.now.core.member.application;

import com.now.core.authentication.application.JwtTokenService;
import com.now.core.authentication.exception.AuthenticationFailedException;
import com.now.core.member.domain.Member;
import com.now.core.member.domain.MemberRepository;
import com.now.core.member.exception.DuplicateMemberException;
import com.now.core.authentication.application.dto.TokenClaims;
import com.now.core.member.application.dto.MemberDuplicateInfo;
import com.now.core.authentication.constants.Authority;
import com.now.common.security.PasswordSecurityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.MessageSourceAccessor;
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
    private final MessageSourceAccessor messageSource;

    /**
     * 회원 정보를 등록하는 메서드
     *
     * @param member 등록할 회원 정보
     * @throws DuplicateMemberException 중복된 회원 정보 예외
     */
    public void registerMember(Member member) {
        MemberDuplicateInfo memberDuplicateInfo = duplicateMemberCheck(member);

        if (memberDuplicateInfo.existsAtLeastOneDuplicate()) {
            throw new DuplicateMemberException(memberDuplicateInfo.generateDuplicateFieldMessages());
        }

        memberRepository.saveMember(member.updatePassword(passwordSecurityManager.encodeWithSalt(member.getPassword())));
    }

    /**
     * 회원의 인증을 처리하고 JWT 토큰을 생성하여 반환
     *
     * @param member 로그인할 회원 정보
     * @return 인증된 회원에게 부여된 JWT(Json Web Token)
     * @throws AuthenticationFailedException 아이디 또는 비밀번호가 틀린 경우 발생하는 예외
     */
    public String generateAuthToken(Member member) {
        Member savedMember = memberRepository.findById(member.getId());

        if (savedMember == null) {
            throw new AuthenticationFailedException(messageSource.getMessage("error.authentication.failed"));
        }

        if (!passwordSecurityManager.matchesWithSalt(member.getPassword(), savedMember.getPassword())) {
            throw new AuthenticationFailedException(messageSource.getMessage("error.authentication.failed"));
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
            throw new AuthenticationFailedException(messageSource.getMessage("error.authentication.memberNotFound"));
        }
        return member;
    }

    /**
     * 중복된 회원 정보를 체크해서 {@link MemberDuplicateInfo}를 반환
     *
     * @param member 체크할 사용자 정보
     * @return 중복된 사용자 정보 결과
     */
    public MemberDuplicateInfo duplicateMemberCheck(Member member) {
        return MemberDuplicateInfo.builder()
                .duplicateId(memberRepository.existsById(member.getId()))
                .duplicateNickname(memberRepository.existsByNickname(member.getNickname()))
                .build();
    }
}
