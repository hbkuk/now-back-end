package com.now.core.authentication.presentation;

import com.now.config.document.utils.RestDocsTestSupport;
import com.now.config.fixtures.member.MemberFixture;
import com.now.core.authentication.application.JwtTokenProvider;
import com.now.core.authentication.application.dto.jwtTokens;
import com.now.core.member.domain.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.servlet.http.Cookie;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static com.now.config.document.snippet.RequestCookiesSnippet.cookieWithName;
import static com.now.config.document.snippet.RequestCookiesSnippet.customRequestHeaderCookies;
import static com.now.config.fixtures.member.MemberFixture.createMember;
import static com.now.config.fixtures.member.MemberFixture.createMemberProfile;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("인증 컨트롤러는")
class AuthenticationControllerTest extends RestDocsTestSupport {

    @Test
    @DisplayName("회원 정보 조회 후 로그인 처리")
    void signIn() throws Exception {
        String memberId = "honi132";
        String password = "testPassword1!";
        String nickname = "Honi";
        String name = "김훈이";

        String requestBody = "{\"id\": \"" + memberId + "\", \"password\": \"" + password + "\"}";
        Member member = createMember(memberId, MemberFixture.SAMPLE_PASSWORD_1);
        Member memberProfile = createMemberProfile(memberId, nickname, name);
        jwtTokens token = jwtTokens.builder()
                .accessToken("Bearer AccessToken")
                .refreshToken("Bearer RefreshToken")
                .build();

        given(authenticationIntegratedService.retrieveMember(member)).willReturn(memberProfile);
        given(authenticationIntegratedService.generateAuthToken(memberProfile)).willReturn(token);

        ResultActions resultActions =
                mockMvc.perform(RestDocumentationRequestBuilders.post("/api/sign-in")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                        .andExpect(status().isOk())

                        .andExpect(cookie().httpOnly(JwtTokenProvider.ACCESS_TOKEN_KEY, true))
                        .andExpect(cookie().httpOnly(JwtTokenProvider.REFRESH_TOKEN_KEY, true))
                        .andExpect(cookie().value(JwtTokenProvider.ACCESS_TOKEN_KEY, URLEncoder.encode(token.getAccessToken(), StandardCharsets.UTF_8)))
                        .andExpect(cookie().value(JwtTokenProvider.REFRESH_TOKEN_KEY, URLEncoder.encode(token.getRefreshToken(), StandardCharsets.UTF_8)))

                        .andExpect(jsonPath("$.id").value(memberId))
                        .andExpect(jsonPath("$.nickname").value(nickname))
                        .andExpect(jsonPath("$.name").value(name));

        resultActions
                .andDo(restDocs.document(
                        requestFields(
                                fieldWithPath("id").description("아이디"),
                                fieldWithPath("password").description("비밀번호")
                        ),
                        responseFields(
                                fieldWithPath("id").description("회원 아이디"),
                                fieldWithPath("nickname").description("회원 닉네임"),
                                fieldWithPath("name").description("회원 성명")
                        ),
                        responseHeaders(
                                headerWithName("Set-Cookie")
                                        .description("쿠키 정보 (access_token, refresh_token)")
                        )
                ));
    }

    @Test
    @DisplayName("토큰 정보가 담긴 쿠키의 만료 시간을 0으로 설정 후 해당 블랙리스트 등록")
    void logout() throws Exception {
        String accessToken = "Bearer AccessToken";
        String refreshToken = "Bearer RefreshToken";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/log-out")
                        .cookie(new Cookie(JwtTokenProvider.ACCESS_TOKEN_KEY, accessToken))
                        .cookie(new Cookie(JwtTokenProvider.REFRESH_TOKEN_KEY, refreshToken)))
                .andExpect(cookie().maxAge("access_token", 0))
                .andExpect(cookie().maxAge("refresh_token", 0))
                .andExpect(status().isOk())

                .andDo(restDocs.document(
                        customRequestHeaderCookies(
                                cookieWithName("access_token").description("액세스 토큰 쿠키 이름"),
                                cookieWithName("refresh_token").description("리프레시 토큰 쿠키 이름")
                        )
                ));
    }

    @Test
    @DisplayName("토큰 확인 후 AccessToken 재발급 처리")
    void refresh() throws Exception {
        String accessToken = "Bearer AccessToken";
        String refreshToken = "Bearer RefreshToken";
        jwtTokens newToken = jwtTokens.builder()
                .accessToken("Bearer newAccessToken")
                .refreshToken(refreshToken)
                .build();

        given(authenticationIntegratedService.refreshTokens(refreshToken)).willReturn(newToken);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/refresh")
                        .cookie(new Cookie(JwtTokenProvider.ACCESS_TOKEN_KEY, accessToken))
                        .cookie(new Cookie(JwtTokenProvider.REFRESH_TOKEN_KEY, refreshToken)))

                .andExpect(cookie().httpOnly(JwtTokenProvider.ACCESS_TOKEN_KEY, true))
                .andExpect(cookie().httpOnly(JwtTokenProvider.REFRESH_TOKEN_KEY, true))

                .andExpect(cookie().value(JwtTokenProvider.ACCESS_TOKEN_KEY, URLEncoder.encode(newToken.getAccessToken(), StandardCharsets.UTF_8)))
                .andExpect(cookie().value(JwtTokenProvider.REFRESH_TOKEN_KEY, URLEncoder.encode(newToken.getRefreshToken(), StandardCharsets.UTF_8)))

                .andExpect(status().isOk())

                .andDo(restDocs.document(
                        customRequestHeaderCookies(
                                cookieWithName("access_token").description("액세스 토큰 쿠키 이름"),
                                cookieWithName("refresh_token").description("리프레시 토큰 쿠키 이름")
                        ),
                        responseHeaders(
                                headerWithName("Set-Cookie")
                                        .description("쿠키 정보 (access_token, refresh_token)")
                        )
                ));
    }
}
