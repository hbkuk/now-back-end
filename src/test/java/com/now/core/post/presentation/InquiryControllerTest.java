package com.now.core.post.presentation;

import com.now.config.document.utils.RestDocsTestSupport;
import com.now.config.fixtures.post.InquiryFixture;
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
import static com.now.config.fixtures.post.InquiryFixture.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class InquiryControllerTest extends RestDocsTestSupport {

    @Test
    @DisplayName("모든 문의 게시글 조회")
    void getAllInquiries() throws Exception {
        // given
        Condition condition = new Condition(5);
        given(inquiryService.getAllInquiries(condition))
                .willReturn(List.of(
                        createNonSecretInquiry(1L, InquiryFixture.SAMPLE_NICKNAME_1, InquiryFixture.SAMPLE_TITLE_1, InquiryFixture.SAMPLE_CONTENT_1),
                        createNonSecretInquiry(2L, InquiryFixture.SAMPLE_NICKNAME_2, InquiryFixture.SAMPLE_TITLE_2, InquiryFixture.SAMPLE_CONTENT_2)
                ));

        // when, then
        ResultActions resultActions =
                mockMvc.perform(RestDocumentationRequestBuilders.get("/api/inquiries")
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
                                fieldWithPath("[]").type(ARRAY).description("문의 게시글 목록"),
                                fieldWithPath("[].postIdx").type(NUMBER).description("게시글 ID"),
                                fieldWithPath("[].title").type(STRING).description("제목"),
                                fieldWithPath("[].memberNickname").type(STRING).description("매니저 닉네임"),
                                fieldWithPath("[].regDate").type(STRING).description("등록일"),
                                fieldWithPath("[].modDate").type(STRING).optional().description("수정일(null 가능)"),
                                fieldWithPath("[].content").type(STRING).optional().description("내용(비밀글 설정 시 null 가능)"),
                                fieldWithPath("[].viewCount").type(NUMBER).description("조회수"),
                                fieldWithPath("[].likeCount").type(NUMBER).description("좋아요 수"),
                                fieldWithPath("[].dislikeCount").type(NUMBER).description("싫어요 수"),
                                fieldWithPath("[].category").type(STRING).description("카테고리"),
                                fieldWithPath("[].secret").type(BOOLEAN).description("비밀글 설정 여부"),
                                fieldWithPath("[].answerManagerNickname").type(STRING).description("답변한 매니저 닉네임").optional()
                        )));
    }

    @Test
    @DisplayName("문의 게시글 응답")
    void getInquiry() throws Exception {
        // given
        Long postIdx = 1L;
        given(inquiryService.getInquiryWithSecretCheck(postIdx))
                .willReturn(createNonSecretInquiry(
                        1L, InquiryFixture.SAMPLE_NICKNAME_1, InquiryFixture.SAMPLE_TITLE_1, InquiryFixture.SAMPLE_CONTENT_1));

        // when, then
        ResultActions resultActions = mockMvc.perform(RestDocumentationRequestBuilders.get("/api/inquiry/{postIdx}", postIdx)
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
                                fieldWithPath("memberNickname").type(STRING).description("작성자 닉네임"),
                                fieldWithPath("regDate").type(STRING).description("등록일"),
                                fieldWithPath("modDate").type(STRING).optional().description("수정일(null 가능)"),
                                fieldWithPath("content").type(STRING).optional().description("내용(비밀글 설정 시 null 가능)"),
                                fieldWithPath("viewCount").type(NUMBER).description("조회수"),
                                fieldWithPath("likeCount").type(NUMBER).description("좋아요 수"),
                                fieldWithPath("dislikeCount").type(NUMBER).description("싫어요 수"),
                                fieldWithPath("category").type(STRING).description("카테고리"),
                                fieldWithPath("secret").type(BOOLEAN).description("비밀글 설정 여부"),
                                fieldWithPath("answerManagerNickname").type(STRING).description("답변한 매니저 닉네임(비밀글 설정 시 null 가능)").optional(),
                                fieldWithPath("answerContent").type(STRING).description("답변 내용(비밀글 설정 시 null 가능)").optional(),
                                fieldWithPath("answerRegDate").type(STRING).description("답변 수정일(비밀글 설정 시 null 가능)").optional()
                        )));
    }

    @Test
    @DisplayName("비밀글 설정된 문의 게시글 응답")
    void getSecretInquiry() throws Exception {
        Long postIdx = 1L;
        String accessToken = "AccessToken";
        given(jwtTokenService.getClaim(accessToken,"id")).willReturn("teter1");
        given(inquiryService.getInquiryWithSecretCheck(anyLong(), anyString(), any()))
                .willReturn(createSecretInquiry(
                        1L, InquiryFixture.SAMPLE_NICKNAME_1, InquiryFixture.SAMPLE_TITLE_1, InquiryFixture.SAMPLE_CONTENT_1));

        // when, then
        ResultActions resultActions = mockMvc.perform(RestDocumentationRequestBuilders.post("/api/inquiry/secret/{postIdx}", postIdx)
                        .header(HttpHeaders.AUTHORIZATION, accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("password", "password"))
                .andExpect(status().isOk());

        resultActions
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("postIdx").description("게시글 ID")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("AccessToken").optional()
                        ),
                        requestParameters(
                                parameterWithName("password").description("비밀글 비밀번호").optional()
                        ),
                        responseFields(
                                fieldWithPath("postIdx").type(NUMBER).description("게시글 ID"),
                                fieldWithPath("title").type(STRING).description("제목"),
                                fieldWithPath("memberNickname").type(STRING).description("작성자 닉네임"),
                                fieldWithPath("regDate").type(STRING).description("등록일"),
                                fieldWithPath("modDate").type(STRING).optional().description("수정일(null 가능)"),
                                fieldWithPath("content").type(STRING).optional().description("내용(비밀글 설정 시 null 가능)"),
                                fieldWithPath("viewCount").type(NUMBER).description("조회수"),
                                fieldWithPath("likeCount").type(NUMBER).description("좋아요 수"),
                                fieldWithPath("dislikeCount").type(NUMBER).description("싫어요 수"),
                                fieldWithPath("category").type(STRING).description("카테고리"),
                                fieldWithPath("secret").type(BOOLEAN).description("비밀글 설정 여부"),
                                fieldWithPath("answerManagerNickname").type(STRING).description("답변한 매니저 닉네임(비밀글 설정 시 null 가능)").optional(),
                                fieldWithPath("answerContent").type(STRING).description("답변 내용(비밀글 설정 시 null 가능)").optional(),
                                fieldWithPath("answerRegDate").type(STRING).description("답변 수정일(비밀글 설정 시 null 가능)").optional()
                        )));
    }

    @Test
    @DisplayName("문의 게시글 등록")
    void registerInquiry() throws Exception {
        String memberId = "member1";
        String accessToken = "Bearer accessToken";

        given(jwtTokenService.getClaim(accessToken, "id")).willReturn(memberId);
        given(jwtTokenService.getClaim(accessToken, "role")).willReturn(Authority.MEMBER.getValue());

        ResultActions resultActions =
                mockMvc.perform(RestDocumentationRequestBuilders.post("/api/inquiry")
                                .header(HttpHeaders.AUTHORIZATION, accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(createInquiryForSave().updatePostIdx(1L))))
                        .andExpect(MockMvcResultMatchers.header().exists(HttpHeaders.LOCATION))
                        .andExpect(MockMvcResultMatchers.status().isCreated());

        resultActions
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("AccessToken")
                        ),
                        requestFields(
                                fieldWithPath("postIdx").ignored(),
                                fieldWithPath("category").description("카테고리").attributes(field("constraints", "필수 선택")),
                                fieldWithPath("title").description("제목").attributes(field("constraints", "길이 100 이하")),
                                fieldWithPath("content").description("내용").attributes(field("constraints", "길이 2000 이하")),
                                fieldWithPath("secret").description("비밀글 설정 여부"),
                                fieldWithPath("password").type(STRING).description("비밀글 설정시 비밀번호").optional()
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("생성된 위치 URI")
                        )
                ));
    }

    @Test
    @DisplayName("문의 게시글 수정")
    void updateInquiry() throws Exception {
        long postIdx = 1L;
        String memberId = "member1";
        String accessToken = "Bearer accessToken";

        given(jwtTokenService.getClaim(accessToken, "id")).willReturn(memberId);
        given(jwtTokenService.getClaim(accessToken, "role")).willReturn(Authority.MEMBER.getValue());

        ResultActions resultActions =
                mockMvc.perform(RestDocumentationRequestBuilders.put("/api/inquiry/{postIdx}", postIdx)
                                .header(HttpHeaders.AUTHORIZATION, accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(createInquiryForSave().updatePostIdx(postIdx))))
                        .andExpect(MockMvcResultMatchers.header().exists(HttpHeaders.LOCATION))
                        .andExpect(MockMvcResultMatchers.status().isCreated());

        resultActions
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("AccessToken")
                        ),
                        requestFields(
                                fieldWithPath("postIdx").description("수정할 게시글 번호"),
                                fieldWithPath("category").description("카테고리").attributes(field("constraints", "필수 선택")),
                                fieldWithPath("title").description("제목").attributes(field("constraints", "길이 100 이하")),
                                fieldWithPath("content").description("내용").attributes(field("constraints", "길이 2000 이하")),
                                fieldWithPath("secret").description("비밀글 설정 여부"),
                                fieldWithPath("password").type(STRING).description("비밀글 설정시 비밀번호").optional()
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("생성된 위치 URI")
                        )
                ));
    }

    @Test
    @DisplayName("문의 게시글 삭제")
    void deleteInquiry() throws Exception {
        Long postIdx = 1L;
        String memberId = "member1";
        String accessToken = "Bearer accessToken";

        given(jwtTokenService.getClaim(accessToken, "id")).willReturn(memberId);
        given(jwtTokenService.getClaim(accessToken, "role")).willReturn(Authority.MEMBER.getValue());

        ResultActions resultActions = mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/inquiry/{postIdx}", postIdx)
                        .header(HttpHeaders.AUTHORIZATION, accessToken))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        resultActions
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("AccessToken")
                        ),
                        pathParameters(
                                parameterWithName("postIdx").description("삭제할 게시글 번호")
                        )
                ));
    }
}