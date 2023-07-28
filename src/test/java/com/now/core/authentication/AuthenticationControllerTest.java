package com.now.core.authentication;

import com.now.config.document.utils.RestDocsTestSupport;
import com.now.config.fixtures.member.MemberFixture;
import com.now.core.authentication.application.JwtTokenService;
import com.now.core.authentication.application.dto.Token;
import com.now.core.member.domain.Member;
import com.now.core.member.presentation.dto.MemberProfile;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static com.now.config.fixtures.member.MemberFixture.createMember;
import static com.now.config.fixtures.member.MemberFixture.createMemberProfile;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AuthenticationControllerTest extends RestDocsTestSupport {

    @Test
    @DisplayName("회원 정보 조회 후 로그인 처리")
    void signIn() throws Exception {
        String memberId = "honi132";
        String password = "testPassword1!";
        String nickname = "Honi";
        String name = "김훈이";

        String requestBody = "{\"id\": \"" + memberId +"\", \"password\": \"" + password + "\"}";
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
    @DisplayName("토큰 확인 후 AccessToken 재발급 처리")
    void refresh() throws Exception {
        String accessToken = "AccessToken";
        String refreshToken = "RefreshToken";
        Token token = Token.builder()
                .accessToken("AccessToken")
                .refreshToken("RefreshToken")
                .build();

        given(jwtTokenService.refreshTokens(refreshToken)).willReturn(token);

        ResultActions resultActions =
                mockMvc.perform(RestDocumentationRequestBuilders.post("/api/refresh")
                                .header(JwtTokenService.ACCESS_TOKEN_HEADER_KEY, accessToken)
                                .header(JwtTokenService.REFRESH_TOKEN_HEADER_KEY, refreshToken))
                        .andExpect(MockMvcResultMatchers.header().exists(HttpHeaders.AUTHORIZATION))
                        .andExpect(MockMvcResultMatchers.header().exists(JwtTokenService.REFRESH_TOKEN_HEADER_KEY))
                        .andExpect(status().isOk());

        resultActions
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("AccessToken"),
                                headerWithName(JwtTokenService.REFRESH_TOKEN_HEADER_KEY).description("유효기간이 만료되지 않은 RefreshToken")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("새로 발급된 AccessToken"),
                                headerWithName(JwtTokenService.REFRESH_TOKEN_HEADER_KEY).description("기존 RefreshToken")
                        )
                ));
    }
}
