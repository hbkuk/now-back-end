package com.now.core.comment.presentation;

import com.now.config.document.utils.RestDocsTestSupport;
import com.now.config.fixtures.member.MemberFixture;
import com.now.core.authentication.application.JwtTokenService;
import com.now.core.authentication.constants.Authority;
import com.now.core.comment.domain.Comment;
import com.now.core.comment.presentation.dto.CommentsResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.servlet.http.Cookie;
import java.util.Arrays;
import java.util.List;

import static com.now.config.document.snippet.RequestCookiesSnippet.cookieWithName;
import static com.now.config.document.snippet.RequestCookiesSnippet.customRequestHeaderCookies;
import static com.now.config.document.utils.RestDocsConfig.field;
import static com.now.config.fixtures.comment.CommentFixture.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("댓글 컨트롤러는")
class CommentControllerTest extends RestDocsTestSupport {

    @Test
    @DisplayName("게시글 번호에 해당하는 모든 댓글 조회")
    void getAllComments() throws Exception {
        Long postIdx = 1L;
        List<Comment> comments = Arrays.asList(
                createComment(postIdx, 1L, MemberFixture.MEMBER1_NICKNAME, "댓글 내용 1"),
                createComment(postIdx, 2L, MemberFixture.MEMBER2_NICKNAME, "댓글 내용 2"),
                createComment(postIdx, 3L, MemberFixture.MEMBER3_NICKNAME, "댓글 내용 3"),
                createComment(postIdx, 4L, MemberFixture.MEMBER4_NICKNAME, "댓글 내용 4"),
                createComment(postIdx, 5L, MemberFixture.MEMBER5_NICKNAME, "댓글 내용 5")
        );
        CommentsResponse commentsResponse = CommentsResponse.builder()
                .comments(comments)
                .build();

        given(commentService.getAllComments(postIdx)).willReturn(commentsResponse);

        // when, then
        ResultActions resultActions = mockMvc.perform(RestDocumentationRequestBuilders.get("/api/posts/{postIdx}/comments", postIdx)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        resultActions
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("postIdx").description("게시글 ID")
                        ),
                        responseFields(
                                fieldWithPath("comments").type(ARRAY).description("댓글 목록"),
                                fieldWithPath("comments[].commentIdx").type(NUMBER).description("댓글 ID"),
                                fieldWithPath("comments[].memberNickname").type(STRING).optional().description("회원 ID"),
                                fieldWithPath("comments[].managerNickname").type(STRING).optional().description("매니저 닉네임"),
                                fieldWithPath("comments[].regDate").type(STRING).description("댓글 등록일"),
                                fieldWithPath("comments[].content").type(STRING).description("댓글 내용"),
                                fieldWithPath("comments[].postIdx").type(NUMBER).description("원글의 ID")
                        )));

    }

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