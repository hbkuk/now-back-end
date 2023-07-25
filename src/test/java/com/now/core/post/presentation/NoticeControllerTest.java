package com.now.core.post.presentation;

import com.now.config.document.utils.RestDocsTestSupport;
import com.now.config.fixtures.post.NoticeFixture;
import com.now.core.authentication.constants.Authority;
import com.now.core.post.presentation.dto.Condition;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static com.now.config.document.utils.RestDocsConfig.field;
import static com.now.config.fixtures.post.NoticeFixture.createNotice;
import static com.now.config.fixtures.post.NoticeFixture.createNoticeForSave;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class NoticeControllerTest extends RestDocsTestSupport {

    @Test
    @DisplayName("모든 공지 게시글 조회")
    void getAllNotices() throws Exception {
        // given
        Condition condition = new Condition(5);
        given(noticeService.getAllNotices(condition))
                .willReturn(List.of(
                        createNotice(1L, NoticeFixture.SAMPLE_NICKNAME_1, NoticeFixture.SAMPLE_TITLE_1, NoticeFixture.SAMPLE_CONTENT_1),
                        createNotice(2L, NoticeFixture.SAMPLE_NICKNAME_2, NoticeFixture.SAMPLE_TITLE_2, NoticeFixture.SAMPLE_CONTENT_2)
                ));

        // when, then
        ResultActions resultActions =
                mockMvc.perform(RestDocumentationRequestBuilders.get("/api/notices")
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("maxNumberOfPosts", String.valueOf(condition.getMaxNumberOfPosts())))
                        .andExpect(status().isOk());

        // then
        resultActions
                .andDo(restDocs.document(
                        requestParameters(
                                parameterWithName("maxNumberOfPosts").description("페이지 개수 제한").optional()
                        ),
                        responseFields(
                                fieldWithPath("[]").type(ARRAY).description("공지 목록"),
                                fieldWithPath("[].postIdx").type(NUMBER).description("게시글 ID"),
                                fieldWithPath("[].title").type(STRING).description("제목"),
                                fieldWithPath("[].managerNickname").type(STRING).description("매니저 닉네임"),
                                fieldWithPath("[].regDate").type(STRING).description("등록일"),
                                fieldWithPath("[].modDate").type(STRING).optional().description("수정일(null 가능)"),
                                fieldWithPath("[].content").type(STRING).description("내용"),
                                fieldWithPath("[].viewCount").type(NUMBER).description("조회수"),
                                fieldWithPath("[].likeCount").type(NUMBER).description("좋아요 수"),
                                fieldWithPath("[].dislikeCount").type(NUMBER).description("싫어요 수"),
                                fieldWithPath("[].category").type(STRING).description("카테고리"),
                                fieldWithPath("[].pinned").type(BOOLEAN).description("상단 고정 여부")
                        )));
    }

    @Test
    @DisplayName("공지 게시글 응답")
    void getNotice() throws Exception {
        // given
        Long postIdx = 1L;
        given(noticeService.getNotice(postIdx))
                .willReturn(createNotice(
                        1L, NoticeFixture.SAMPLE_NICKNAME_1, NoticeFixture.SAMPLE_TITLE_1, NoticeFixture.SAMPLE_CONTENT_1));

        // when, then
        ResultActions resultActions = mockMvc.perform(RestDocumentationRequestBuilders.get("/api/notices/{postIdx}", postIdx)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        resultActions
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("postIdx").description("게시글 ID")
                        ),
                        responseFields(
                                fieldWithPath("postIdx").type(NUMBER).description("게시글 ID"),
                                fieldWithPath("title").type(STRING).description("제목"),
                                fieldWithPath("managerNickname").type(STRING).description("작성자 닉네임"),
                                fieldWithPath("regDate").type(STRING).description("등록일"),
                                fieldWithPath("modDate").type(STRING).optional().description("수정일(null 가능)"),
                                fieldWithPath("content").type(STRING).description("내용"),
                                fieldWithPath("viewCount").type(NUMBER).description("조회수"),
                                fieldWithPath("likeCount").type(NUMBER).description("좋아요 수"),
                                fieldWithPath("dislikeCount").type(NUMBER).description("싫어요 수"),
                                fieldWithPath("category").type(STRING).description("카테고리"),
                                fieldWithPath("pinned").type(BOOLEAN).description("상단 고정 여부")
                        )));
    }
    
    // TODO: BindException 처리
    @Test
    @DisplayName("공지 게시글 등록")
    void registerNotice() throws Exception {
        String managerId = "manager1";
        String accessToken = "Bearer accessToken";

        given(jwtTokenService.getClaim(accessToken, "id")).willReturn(managerId);
        given(jwtTokenService.getClaim(accessToken, "role")).willReturn(Authority.MANAGER.getValue());

        ResultActions resultActions =
                mockMvc.perform(RestDocumentationRequestBuilders.post("/api/manager/notices")
                                .header(HttpHeaders.AUTHORIZATION, accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(createNoticeForSave().updatePostIdx(1L))))
                        .andExpect(MockMvcResultMatchers.header().exists(HttpHeaders.LOCATION))
                        .andExpect(MockMvcResultMatchers.status().isCreated());

        resultActions
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("AccessToken")
                        ),
                        requestFields(
                                fieldWithPath("postIdx").ignored(),
                                fieldWithPath("category").description("카테고리"),
                                fieldWithPath("title").description("제목").attributes(field("constraints", "길이 100 이하")),
                                fieldWithPath("content").description("내용").attributes(field("constraints", "길이 2000 이하")),
                                fieldWithPath("pinned").description("상단 고정 여부")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("생성된 위치 URI")
                        )
                ));
    }

    @Test
    @DisplayName("공지 게시글 수정")
    void updateNotice() throws Exception {
        Long postIdx = 1L;
        String managerId = "manager1";
        String accessToken = "Bearer accessToken";

        given(jwtTokenService.getClaim(accessToken, "id")).willReturn(managerId);
        given(jwtTokenService.getClaim(accessToken, "role")).willReturn(Authority.MANAGER.getValue());

        ResultActions resultActions =
                mockMvc.perform(RestDocumentationRequestBuilders.put("/api/manager/notices/{postIdx}", postIdx)
                                .header(HttpHeaders.AUTHORIZATION, accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(createNoticeForSave().updatePostIdx(postIdx))))
                        .andExpect(MockMvcResultMatchers.header().exists(HttpHeaders.LOCATION))
                        .andExpect(MockMvcResultMatchers.status().isCreated());

        resultActions
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("AccessToken")
                        ),
                        requestFields(
                                fieldWithPath("postIdx").description("게시글 번호"),
                                fieldWithPath("category").description("카테고리"),
                                fieldWithPath("title").description("제목").attributes(field("constraints", "길이 100 이하")),
                                fieldWithPath("content").description("내용").attributes(field("constraints", "길이 2000 이하")),
                                fieldWithPath("pinned").description("상단 고정 여부")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("생성된 위치 URI")
                        )
                ));
    }

    @Test
    @DisplayName("공지 게시글 삭제")
    void deleteNotice() throws Exception {
        Long postIdx = 1L;
        String managerId = "manager1";
        String token = "Bearer accessToken";

        given(jwtTokenService.getClaim(token, "id")).willReturn(managerId);
        given(jwtTokenService.getClaim(token, "role")).willReturn(Authority.MANAGER.getValue());

        ResultActions resultActions = mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/manager/notices/{postIdx}", postIdx)
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        resultActions
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("AccessToken")
                        ),
                        pathParameters(
                                parameterWithName("postIdx").description("게시글 번호")
                        )
                ));
    }
}