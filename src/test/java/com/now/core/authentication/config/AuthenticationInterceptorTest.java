package com.now.core.authentication.config;

import com.now.config.document.utils.ControllerTest;
import com.now.config.fixtures.member.MemberFixture;
import com.now.config.fixtures.post.dto.PostReactionFixture;
import com.now.core.authentication.application.JwtTokenProvider;
import com.now.core.post.common.presentation.dto.PostReaction;
import com.now.core.post.common.presentation.dto.constants.Reaction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.servlet.http.Cookie;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthenticationInterceptorTest extends ControllerTest {

    @Test
    @DisplayName("클라이언트가 블랙리스트에 담긴 토큰으로 요청한다면, InvalidTokenException이 발생한다")
    void isAccessTokenBlacklisted() throws Exception {
        // given
        Long postIdx = 1L;
        String memberId = MemberFixture.MEMBER1_ID;
        String accessToken = "Bearer accessToken";
        PostReaction postReactionResponse = PostReactionFixture.createPostReaction(postIdx, memberId, Reaction.DISLIKE);

        given(jwtTokenProvider.getClaim(accessToken, "id")).willReturn(memberId);
        given(tokenBlackList.isAccessTokenBlacklisted(accessToken)).willReturn(true);

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/posts/{postIdx}/reaction", postIdx)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie(JwtTokenProvider.ACCESS_TOKEN_KEY, accessToken))
                        .content(objectMapper.writeValueAsString(postReactionResponse)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    @DisplayName("클라이언트가 로그아웃을 요청한 후 해당 토큰이 전달된다면, InvalidTokenException이 발생한다")
    void isAccessTokenBlacklistedWhenLogOut() throws Exception {
        // given
        Long postIdx = 1L;
        String memberId = MemberFixture.MEMBER1_ID;
        String accessToken = "Bearer accessToken";
        String refreshToken = "Bearer refreshToken";
        PostReaction postReactionResponse = PostReactionFixture.createPostReaction(postIdx, memberId, Reaction.DISLIKE);
        given(jwtTokenProvider.getClaim(accessToken, "id")).willReturn(memberId);

        // when
        mockMvc.perform(MockMvcRequestBuilders.post("/api/log-out")
                        .cookie(new Cookie(JwtTokenProvider.ACCESS_TOKEN_KEY, accessToken))
                        .cookie(new Cookie(JwtTokenProvider.REFRESH_TOKEN_KEY, refreshToken)))
                .andExpect(cookie().maxAge("access_token", 0))
                .andExpect(cookie().maxAge("refresh_token", 0))
                .andExpect(status().isOk());

        given(tokenBlackList.isAccessTokenBlacklisted(accessToken)).willReturn(true);

        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/posts/{postIdx}/reaction", postIdx)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie(JwtTokenProvider.ACCESS_TOKEN_KEY, accessToken))
                        .content(objectMapper.writeValueAsString(postReactionResponse)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());

        // then
        verify(authenticationIntegratedService, times(1)).revokeTokens(accessToken, refreshToken);
    }
}
