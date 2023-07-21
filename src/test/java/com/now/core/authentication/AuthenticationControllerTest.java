package com.now.core.authentication;

import com.now.config.document.utils.RestDocsTestSupport;
import com.now.config.fixtures.member.MemberFixture;
import com.now.core.authentication.application.dto.Token;
import com.now.core.member.domain.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static com.now.config.fixtures.member.MemberFixture.createMember;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthenticationControllerTest extends RestDocsTestSupport {

    @Test
    @DisplayName("회원 정보 조회 후 로그인 처리")
    void signIn() throws Exception {
        String requestBody = "{\"id\": \"honi132\", \"password\": \"testPassword1!\"}";
        Member member = createMember(MemberFixture.SAMPLE_MEMBER_ID_1,
                MemberFixture.SAMPLE_PASSWORD_1);
        Token token = Token.builder()
                .accessToken("AccessToken")
                .refreshToken("RefreshToken")
                .build();

        given(memberService.generateAuthToken(member)).willReturn(token);


        ResultActions resultActions =
                mockMvc.perform(RestDocumentationRequestBuilders.post("/api/sign-in")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                        .andExpect(MockMvcResultMatchers.header().exists(HttpHeaders.AUTHORIZATION))
                        .andExpect(MockMvcResultMatchers.header().exists(jwtTokenService.REFRESH_TOKEN_HEADER_KEY))
                        .andExpect(status().isOk());

        resultActions
                .andDo(restDocs.document(
                        requestFields(
                                fieldWithPath("id").description("회원 아이디"),
                                fieldWithPath("password").description("비밀번호")
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
                                .header(jwtTokenService.ACCESS_TOKEN_HEADER_KEY, accessToken)
                                .header(jwtTokenService.REFRESH_TOKEN_HEADER_KEY, refreshToken))
                        .andExpect(MockMvcResultMatchers.header().exists(HttpHeaders.AUTHORIZATION))
                        .andExpect(MockMvcResultMatchers.header().exists(jwtTokenService.REFRESH_TOKEN_HEADER_KEY))
                        .andExpect(status().isOk());

        resultActions
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("유효기간이 만료된 AccessToken"),
                                headerWithName(jwtTokenService.REFRESH_TOKEN_HEADER_KEY).description("유효기간이 만료되지 않은 RefreshToken")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("새로 발급된 AccessToken"),
                                headerWithName(jwtTokenService.REFRESH_TOKEN_HEADER_KEY).description("RefreshToken")
                        )
                ));
    }
}
