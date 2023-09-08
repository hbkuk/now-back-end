package com.now.core.authentication.presentation;

import com.now.config.document.utils.RestDocsTestSupport;
import com.now.config.fixtures.member.MemberFixture;
import com.now.core.authentication.application.JwtTokenProvider;
import com.now.core.authentication.application.dto.Token;
import com.now.core.member.domain.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.servlet.http.Cookie;

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
        Token token = Token.builder()
                .accessToken("AccessToken")
                .refreshToken("RefreshToken")
                .build();

        given(memberService.validateCredentialsAndRetrieveMember(member)).willReturn(memberProfile);
        given(memberService.generateAuthToken(memberProfile)).willReturn(token);

        ResultActions resultActions =
                mockMvc.perform(RestDocumentationRequestBuilders.post("/api/sign-in")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                        .andExpect(status().isOk())

                        .andExpect(cookie().httpOnly("access_token", true))
                        .andExpect(cookie().httpOnly("refresh_token", true))
                        .andExpect(cookie().value("access_token", token.getAccessToken()))
                        .andExpect(cookie().value("refresh_token", token.getRefreshToken()))

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
        String accessToken = "AccessToken";
        String refreshToken = "RefreshToken";

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
        String accessToken = "AccessToken";
        String refreshToken = "RefreshToken";
        Token newToken = Token.builder()
                .accessToken("newAccessToken")
                .refreshToken(refreshToken)
                .build();

        given(jwtTokenProvider.refreshTokens(refreshToken)).willReturn(newToken);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/refresh")
                        .cookie(new Cookie(JwtTokenProvider.ACCESS_TOKEN_KEY, accessToken))
                        .cookie(new Cookie(JwtTokenProvider.REFRESH_TOKEN_KEY, refreshToken)))
                .andExpect(cookie().httpOnly("access_token", true))
                .andExpect(cookie().httpOnly("refresh_token", true))
                .andExpect(cookie().value("access_token", newToken.getAccessToken()))
                .andExpect(cookie().value("refresh_token", newToken.getRefreshToken()))
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
