package com.now.core.admin.authentication.presentation;

import com.now.config.document.utils.RestDocsTestSupport;
import com.now.core.admin.authentication.domain.Manager;
import com.now.core.authentication.application.JwtTokenProvider;
import com.now.core.authentication.application.dto.jwtTokens;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.servlet.http.Cookie;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static com.now.config.fixtures.manager.ManagerFixture.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("매니저 인증 컨트롤러는")
class ManagerAuthenticationControllerTest extends RestDocsTestSupport {

    @Test
    @DisplayName("매니저 정보 조회 후 로그인 처리")
    void signIn() throws Exception {
        // given
        String managerId = MANAGER1_ID;
        String nickname = MANAGER1_NICKNAME;
        String name = MANAGER1_NAME;
        String password = "testPassword1!";
        String requestBody = "{\"id\": \"" + managerId + "\", \"password\": \"" + password + "\"}";
        Manager manager = createManager(managerId, password);
        Manager managerProfile = createManagerProfile(managerId, nickname, name);
        jwtTokens token = jwtTokens.builder()
                .accessToken("Bearer AccessToken")
                .refreshToken("Bearer RefreshToken")
                .build();

        given(managerAuthenticationService.retrieveManager(manager)).willReturn(managerProfile);
        given(managerAuthenticationService.generateAuthToken(managerProfile)).willReturn(token);

        // when, then
        ResultActions resultActions =
                mockMvc.perform(RestDocumentationRequestBuilders.post("/api/manager/sign-in")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                        .andExpect(status().isOk())

                        .andExpect(cookie().httpOnly(JwtTokenProvider.ACCESS_TOKEN_KEY, true))
                        .andExpect(cookie().httpOnly(JwtTokenProvider.REFRESH_TOKEN_KEY, true))
                        .andExpect(cookie().value(JwtTokenProvider.ACCESS_TOKEN_KEY, URLEncoder.encode(token.getAccessToken(), StandardCharsets.UTF_8)))
                        .andExpect(cookie().value(JwtTokenProvider.REFRESH_TOKEN_KEY, URLEncoder.encode(token.getRefreshToken(), StandardCharsets.UTF_8)))

                        .andExpect(jsonPath("$.id").value(managerId))
                        .andExpect(jsonPath("$.nickname").value(nickname));
    }
    
    @Test
    @DisplayName("매니저 정보 반환")
    void me() throws Exception {
        // given
        String accessToken = "Bearer accessToken";
        String managerId = MANAGER1_ID;
        Manager managerProfile = createManagerProfile(managerId, MANAGER1_NICKNAME, MANAGER1_NAME);
        jwtTokens token = jwtTokens.builder()
                .accessToken("Bearer AccessToken")
                .refreshToken("Bearer RefreshToken")
                .build();

        given(jwtTokenProvider.getClaim(accessToken, "id")).willReturn(managerId);
        given(jwtTokenProvider.getClaim(accessToken, "role")).willReturn("MANAGER");
        given(managerAuthenticationService.getManager(managerId)).willReturn(managerProfile);

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/manager/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie(JwtTokenProvider.ACCESS_TOKEN_KEY, accessToken)))
                .andExpect(MockMvcResultMatchers.status().isOk())

                .andExpect(jsonPath("$.id").value(managerId))
                .andExpect(jsonPath("$.nickname").value(MANAGER1_NICKNAME));
    }
}
