package com.now.core.admin.authentication.presentation;

import com.now.config.document.utils.ControllerTest;
import com.now.core.authentication.application.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.servlet.http.Cookie;

import static com.now.config.fixtures.manager.ManagerFixture.MANAGER1_ID;
import static com.now.config.fixtures.manager.ManagerFixture.createManager;
import static org.mockito.BDDMockito.given;

class ManagerAuthenticationInterceptorTest extends ControllerTest {

    @Test
    @DisplayName("accessToken에서 추출한 Claim 중 role이 Manager가 아니라면, InvalidAuthenticationException이 발생한다")
    void roleNotManager() throws Exception {
        // given
        String accessToken = "Bearer accessToken";

        given(jwtTokenProvider.getClaim(accessToken, "role")).willReturn("MEMBER");

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/manager/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie(JwtTokenProvider.ACCESS_TOKEN_KEY, accessToken)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    @DisplayName("accessToken에서 추출한 Claim 중 role이 Manager라면, 정상적으로 Http 200 코드를 응답한다 ")
    void roleManager() throws Exception {
        // given
        String accessToken = "Bearer accessToken";
        String managerId = MANAGER1_ID;

        given(jwtTokenProvider.getClaim(accessToken, "role")).willReturn("MANAGER");
        given(jwtTokenProvider.getClaim(accessToken, "id")).willReturn(managerId);
        given(managerAuthenticationService.getManager(managerId)).willReturn(createManager(managerId));

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/manager/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie(JwtTokenProvider.ACCESS_TOKEN_KEY, accessToken)))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
