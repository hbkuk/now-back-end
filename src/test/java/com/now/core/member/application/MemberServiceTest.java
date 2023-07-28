package com.now.core.member.application;

import com.now.NowApplication;
import com.now.common.exception.ErrorType;
import com.now.common.security.PasswordSecurityManager;
import com.now.core.authentication.application.JwtTokenService;
import com.now.core.authentication.application.dto.TokenClaims;
import com.now.core.authentication.constants.Authority;
import com.now.core.member.domain.Member;
import com.now.core.member.domain.MemberRepository;
import com.now.core.member.exception.DuplicateMemberInfoException;
import com.now.core.member.exception.InvalidMemberException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = NowApplication.class)
public class MemberServiceTest {

    @Autowired private MemberService memberService;
    @MockBean private MemberRepository memberRepository;
    @MockBean private PasswordSecurityManager passwordSecurityManager;
    @MockBean private JwtTokenService jwtTokenService;

    Member memberA = null;

    @BeforeEach
    void createMember() {
        memberA = Member.builder()
                .id("testerA")
                .password("testPasswordA")
                .name("testNameA")
                .nickname("testNickNameA")
                .build();
    }

    @Nested
    @DisplayName("회원가입은 회원 정보를 받아서")
    class Member_Register_of {

        @Test
        @DisplayName("특정 데이터와 중복되는 필드가 하나라도 있다면 DuplicateMemberException 예외를 던진다.")
        void duplicateMemberInfoException() {
            when(memberRepository.existsById(anyString())).thenReturn(true);
            when(memberRepository.existsByNickname(anyString())).thenReturn(true);

            assertThatExceptionOfType(DuplicateMemberInfoException.class)
                    .isThrownBy(() -> {
                        memberService.registerMember(memberA);
                    })
                    .withMessage(ErrorType.DUPLICATE_MEMBER_INFO_ID_AND_NICKNAME.getMessage());
        }

        @Test
        @DisplayName("특정 데이터와 중복되는 정보가 없다면 memberRepository의 saveMember 메서드가 실행된다.")
        void saveMember() {
            when(memberRepository.existsById(anyString())).thenReturn(false);
            when(memberRepository.existsByNickname(anyString())).thenReturn(false);

            memberService.registerMember(memberA);

            verify(memberRepository, times(1)).saveMember(memberA);
        }
    }

    @Nested
    @DisplayName("회원 로그인은 회원 정보를 받아서")
    class Member_Auth_of {

        @Test
        @DisplayName("동일한 회원 정보가 데이터베이스에 없다면 InvalidAuthenticationException 예외를 던진다.")
        void validateCredentialsAndRetrieveMember_1() {
            when(memberRepository.findById(memberA.getId())).thenReturn(null);

            assertThatExceptionOfType(InvalidMemberException.class)
                    .isThrownBy(() -> {
                        memberService.validateCredentialsAndRetrieveMember(memberA);
                    })
                    .withMessage(ErrorType.NOT_FOUND_MEMBER.getMessage());

        }

        @Test
        @DisplayName("저장된 회원 정보가 데이터베이스에 있고, 비밀번호가 동일하지 않다면 AuthenticationFailedException 예외를 던진다.")
        void validateCredentialsAndRetrieveMember_2() {
            when(memberRepository.findById(anyString())).thenReturn(memberA);
            when(passwordSecurityManager.matchesWithSalt(anyString(), anyString())).thenReturn(false);

            assertThatExceptionOfType(InvalidMemberException.class)
                    .isThrownBy(() -> {
                        memberService.validateCredentialsAndRetrieveMember(memberA);
                    })
                    .withMessage(ErrorType.NOT_FOUND_MEMBER.getMessage());
        }

        @Test
        @DisplayName("validateCredentialsAndRetrieveMember 메서드에서 검증 후 generateAuthToken 메서드를 통해 토큰을 발행한다")
        void generateAuthToken() {
            // Authentication 컨트롤러에서 로직변경으로 인한 주석처리
            //when(memberRepository.findById(anyString())).thenReturn(memberA);
            //when(passwordSecurityManager.matchesWithSalt(anyString(), anyString())).thenReturn(true);

            memberService.generateAuthToken(memberA);

            verify(jwtTokenService, times(1)).createAccessToken(TokenClaims.create(Map.of(
                    "id", memberA.getId(), "nickname", memberA.getNickname(), "role", Authority.MEMBER.getValue())));
            verify(jwtTokenService, times(1)).createRefreshToken(TokenClaims.create(Map.of(
                    "id", memberA.getId(), "nickname", memberA.getNickname(), "role", Authority.MEMBER.getValue())));
        }
    }
}
