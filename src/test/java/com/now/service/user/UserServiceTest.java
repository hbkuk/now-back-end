package com.now.service.user;

import com.now.core.member.domain.Member;
import com.now.core.authentication.exception.AuthenticationFailedException;
import com.now.core.member.exception.DuplicateMemberException;
import com.now.core.member.domain.MemberRepository;
import com.now.core.authentication.application.JwtTokenService;
import com.now.common.security.PasswordSecurityManager;
import com.now.core.member.application.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.support.MessageSourceAccessor;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = NowApplication.class)
public class UserServiceTest {

    @Autowired private MemberService userService;
    @MockBean private MemberRepository userRepository;
    @MockBean private PasswordSecurityManager passwordSecurityManager;
    @MockBean private JwtTokenService jwtTokenService;
    @MockBean private MessageSourceAccessor messageSource;

    Member userA = null;

    @BeforeEach
    void createUser() {
        userA = Member.builder()
                .id("testerA")
                .password("testPasswordA")
                .name("testNameA")
                .nickname("testNickNameA")
                .build();
    }

    @Nested
    @DisplayName("사용자 등록(회원가입)은 사용자 정보를 받아서")
    class User_Register_of {

        @Test
        @DisplayName("특정 데이터와 중복되는 필드가 하나라도 있다면 DuplicateUserException 예외를 던진다.")
        void test1() {
            when(userRepository.existsById(anyString())).thenReturn(true);
            when(userRepository.existsByNickname(anyString())).thenReturn(true);

            assertThatExceptionOfType(DuplicateMemberException.class)
                    .isThrownBy(() -> {
                        userService.registerMember(userA);
                    });
        }

        @Test
        @DisplayName("특정 데이터와 중복되는 정보가 없다면 userRepository의 insert 메서드가 실행된다.")
        void test2() {
            when(userRepository.existsById(anyString())).thenReturn(false);
            when(userRepository.existsByNickname(anyString())).thenReturn(false);

            userService.registerMember(userA);

            verify(userRepository, times(1)).saveMember(userA);
        }
    }

    @Nested
    @DisplayName("사용자 인증(로그인)은 사용자 정보를 받아서")
    class User_Auth_of {

        @Test
        @DisplayName("저장된 사용자 정보가 데이터베이스에 없다면 AuthenticationFailedException 예외를 던진다.")
        void test3() {
            when(userRepository.findById(userA.getId())).thenReturn(null);

            assertThatExceptionOfType(AuthenticationFailedException.class)
                    .isThrownBy(() -> {
                        userService.generateAuthToken(userA);
                    })
                    .withMessage(messageSource.getMessage("error.authentication.failed"));

        }

        @Test
        @DisplayName("저장된 사용자 정보가 데이터베이스에 있고, 비밀번호가 동일하다면 AuthenticationFailedException 예외를 던진다.")
        void test4() {
            when(userRepository.findById(anyString())).thenReturn(userA);
            when(passwordSecurityManager.matchesWithSalt(anyString(), anyString())).thenReturn(false);

            assertThatExceptionOfType(AuthenticationFailedException.class)
                    .isThrownBy(() -> {
                        userService.generateAuthToken(userA);
                    })
                    .withMessage(messageSource.getMessage("error.authentication.failed"));
        }

        @Test
        @DisplayName("저장된 사용자 정보가 데이터베이스에 있고, 비밀번호도 동일하다면 최종적으로 jwtTokenManager의 create 메서드가 실행된다.")
        void test5() {
            when(userRepository.findById(anyString())).thenReturn(userA);
            when(passwordSecurityManager.matchesWithSalt(anyString(), anyString())).thenReturn(true);

            userService.generateAuthToken(userA);

            InOrder inOrder = inOrder(userRepository, passwordSecurityManager, jwtTokenService);
            inOrder.verify(userRepository, times(1)).findById(anyString());
            inOrder.verify(passwordSecurityManager, times(1)).matchesWithSalt(anyString(), anyString());
            inOrder.verify(jwtTokenService, times(1)).create(any());
        }
    }
}
