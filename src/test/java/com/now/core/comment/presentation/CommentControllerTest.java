package com.now.core.comment.presentation;

import com.now.config.document.utils.RestDocsTestSupport;
import com.now.core.authentication.application.JwtTokenService;
import com.now.core.authentication.constants.Authority;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.servlet.http.Cookie;

import static com.now.common.snippet.RequestCookiesSnippet.cookieWithName;
import static com.now.common.snippet.RequestCookiesSnippet.customRequestHeaderCookies;
import static com.now.config.document.utils.RestDocsConfig.field;
import static com.now.config.fixtures.comment.CommentFixture.createCommentForSave;
import static com.now.config.fixtures.comment.CommentFixture.createCommentForUpdate;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;

@DisplayName("댓글 컨트롤러는")
class CommentControllerTest extends RestDocsTestSupport {

    @Test
    @DisplayName("댓글 등록")
    void registerComment() throws Exception {
        Long postIdx = 1L;
        String memberId = "member1";
        String accessToken = "Bearer accessToken";

        given(jwtTokenService.getClaim(accessToken, "id")).willReturn(memberId);
        given(jwtTokenService.getClaim(accessToken, "role")).willReturn(Authority.MEMBER.getValue());

        ResultActions resultActions =
                mockMvc.perform(RestDocumentationRequestBuilders.post("/api/posts/{postIdx}/comments", postIdx)
                                .cookie(new Cookie(JwtTokenService.ACCESS_TOKEN_KEY, accessToken))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(createCommentForSave())))
                        .andExpect(MockMvcResultMatchers.status().isCreated());

        resultActions
                .andDo(restDocs.document(
                        customRequestHeaderCookies(
                                cookieWithName(JwtTokenService.ACCESS_TOKEN_KEY).description("액세스 토큰")
                        ),
                        pathParameters(
                                parameterWithName("postIdx").description("원글 ID")
                        ),
                        requestFields(
                                fieldWithPath("memberNickname").description("회원 닉네임"),
                                fieldWithPath("content").description("댓글 내용").attributes(field("constraints", "길이 2000 이하")),
                                fieldWithPath("regDate").description("생성일"),
                                fieldWithPath("postIdx").description("원글 ID")
                        )
                ));
    }

    @Test
    @DisplayName("댓글 수정")
    void updateComment() throws Exception {
        long postIdx = 1L;
        long commentIdx = 100L;
        String memberId = "member1";
        String accessToken = "Bearer accessToken";

        given(jwtTokenService.getClaim(accessToken, "id")).willReturn(memberId);
        given(jwtTokenService.getClaim(accessToken, "role")).willReturn(Authority.MEMBER.getValue());

        ResultActions resultActions =
                mockMvc.perform(RestDocumentationRequestBuilders.put("/api/posts/{postIdx}/comments/{commentIdx}", postIdx, commentIdx)
                                .cookie(new Cookie(JwtTokenService.ACCESS_TOKEN_KEY, accessToken))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(createCommentForUpdate(commentIdx))))
                        .andExpect(MockMvcResultMatchers.status().isCreated());

        resultActions
                .andDo(restDocs.document(
                        customRequestHeaderCookies(
                                cookieWithName(JwtTokenService.ACCESS_TOKEN_KEY).description("액세스 토큰")
                        ),
                        pathParameters(
                                parameterWithName("postIdx").description("원글 ID"),
                                parameterWithName("commentIdx").description("댓글 ID")
                        ),
                        requestFields(
                                fieldWithPath("commentIdx").description("댓글 ID"),
                                fieldWithPath("memberNickname").description("회원 닉네임"),
                                fieldWithPath("content").description("댓글 내용").attributes(field("constraints", "길이 2000 이하")),
                                fieldWithPath("regDate").description("생성일"),
                                fieldWithPath("postIdx").description("원글 ID")
                        )
                ));
    }

    @Test
    @DisplayName("댓글 삭제")
    void deleteComment() throws Exception {
        Long postIdx = 1L;
        long commentIdx = 100L;
        String memberId = "member1";
        String accessToken = "Bearer accessToken";

        given(jwtTokenService.getClaim(accessToken, "id")).willReturn(memberId);
        given(jwtTokenService.getClaim(accessToken, "role")).willReturn(Authority.MEMBER.getValue());

        ResultActions resultActions = mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/posts/{postIdx}/comments/{commentIdx}", postIdx, commentIdx)
                    .cookie(new Cookie(JwtTokenService.ACCESS_TOKEN_KEY, accessToken)))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        resultActions
                .andDo(restDocs.document(
                        customRequestHeaderCookies(
                                cookieWithName(JwtTokenService.ACCESS_TOKEN_KEY).description("액세스 토큰")
                        ),
                        pathParameters(
                                parameterWithName("postIdx").description("원글 ID"),
                                parameterWithName("commentIdx").description("댓글 ID")
                        )
                ));
    }
}