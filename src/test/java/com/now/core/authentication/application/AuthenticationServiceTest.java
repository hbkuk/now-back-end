package com.now.core.authentication.application;

import com.now.NowApplication;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = NowApplication.class)
@DisplayName("인증 서비스 객체는")
class AuthenticationServiceTest {

    @Autowired AuthenticationService authenticationService;
    @MockBean TokenBlackList tokenBlacklist;

    @Test
    @DisplayName("로그아웃 메서드는 토큰을 전달받아 블랙리스트에 추가한다")
    void testLogout() {
        // Given
        String accessToken = "sampleAccessToken";
        String refreshToken = "sampleRefreshToken";

        // When
        authenticationService.logout(accessToken, refreshToken);

        // Then
        verify(tokenBlacklist, times(1)).addToAccessTokenBlacklist(accessToken);
        verify(tokenBlacklist, times(1)).addToRefreshTokenBlacklist(refreshToken);
    }
}
