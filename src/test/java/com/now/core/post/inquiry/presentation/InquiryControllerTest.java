package com.now.core.post.inquiry.presentation;

import com.now.config.document.utils.RestDocsTestSupport;
import com.now.config.fixtures.post.InquiryFixture;
import com.now.core.authentication.application.JwtTokenProvider;
import com.now.core.authentication.constants.Authority;
import com.now.core.category.domain.constants.Category;
import com.now.core.post.common.presentation.dto.Condition;
import com.now.core.post.inquiry.domain.Inquiry;
import com.now.core.post.inquiry.domain.constants.PrivacyUpdateOption;
import com.now.core.post.inquiry.presentation.dto.InquiriesResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.servlet.http.Cookie;
import java.util.List;

import static com.now.config.document.snippet.RequestCookiesSnippet.cookieWithName;
import static com.now.config.document.snippet.RequestCookiesSnippet.customRequestHeaderCookies;
import static com.now.config.document.utils.RestDocsConfig.field;
import static com.now.config.fixtures.post.InquiryFixture.*;
import static com.now.config.fixtures.post.dto.ConditionFixture.createCondition;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("문의 컨트롤러는")
class InquiryControllerTest extends RestDocsTestSupport {

    @Test
    @DisplayName("모든 문의 게시글 조회")
    void getAllInquiries() throws Exception {
        // given
        Condition condition = createCondition(Category.SERVICE).updatePage();

        InquiriesResponse inquiriesResponse = InquiriesResponse.builder()
                .inquiries(List.of(
                        createNonSecretInquiry(1L, InquiryFixture.SAMPLE_NICKNAME_1, InquiryFixture.SAMPLE_TITLE_1, InquiryFixture.SAMPLE_CONTENT_1),
                        createNonSecretInquiry(2L, InquiryFixture.SAMPLE_NICKNAME_2, InquiryFixture.SAMPLE_TITLE_2, InquiryFixture.SAMPLE_CONTENT_2)
                ))
                .page(createCondition(Category.SERVICE).updatePage().getPage().calculatePageInfo(2L))
                .build();

        given(inquiryIntegratedService.getAllInquiriesWithPageInfo(condition.updatePage())).willReturn(inquiriesResponse);

        // when, then
        ResultActions resultActions =
                mockMvc.perform(RestDocumentationRequestBuilders.get("/api/inquiries")
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("startDate", condition.getStartDate())
                                .param("endDate", condition.getEndDate())
                                .param("category", condition.getCategory().name())
                                .param("keyword", condition.getKeyword())
                                .param("sort", condition.getSort().name())
                                .param("maxNumberOfPosts", String.valueOf(condition.getMaxNumberOfPosts()))
                                .param("pageNo", String.valueOf(condition.getPageNo())))
                        .andExpect(status().isOk());

        // then
        resultActions
                .andDo(restDocs.document(
                        requestParameters(
                                parameterWithName("startDate").description("시작 날짜").optional(),
                                parameterWithName("endDate").description("종료 날짜").optional(),
                                parameterWithName("category").description("카테고리").optional(),
                                parameterWithName("keyword").description("키워드").optional(),
                                parameterWithName("sort").description("정렬").optional(),
                                parameterWithName("maxNumberOfPosts").description("페이지 개수 제한").optional(),
                                parameterWithName("pageNo").description("페이지 번호").optional()
                        ),
                        responseFields(
                                fieldWithPath("inquiries[]").type(ARRAY).description("문의 게시글 목록"),
                                fieldWithPath("inquiries[].postGroup").type(STRING).description("게시물 그룹 코드"),
                                fieldWithPath("inquiries[].postIdx").type(NUMBER).description("게시글 ID"),
                                fieldWithPath("inquiries[].title").type(STRING).description("제목"),
                                fieldWithPath("inquiries[].memberNickname").type(STRING).description("회원 닉네임"),
                                fieldWithPath("inquiries[].regDate").type(STRING).description("등록일"),
                                fieldWithPath("inquiries[].modDate").type(STRING).optional().description("수정일(null 가능)"),
                                fieldWithPath("inquiries[].content").type(STRING).optional().description("내용(비밀글 설정 시 null 가능)"),
                                fieldWithPath("inquiries[].viewCount").type(NUMBER).description("조회수"),
                                fieldWithPath("inquiries[].likeCount").type(NUMBER).description("좋아요 수"),
                                fieldWithPath("inquiries[].dislikeCount").type(NUMBER).description("싫어요 수"),
                                fieldWithPath("inquiries[].category").type(STRING).description("카테고리"),
                                fieldWithPath("inquiries[].secret").type(BOOLEAN).description("비밀글 설정 여부"),
                                fieldWithPath("inquiries[].answerManagerNickname").type(STRING).description("답변한 매니저 닉네임").optional(),
                                fieldWithPath("inquiries[].inquiryStatus").type(STRING).description("답변 상태"),

                                fieldWithPath("inquiries[].comments").type(ARRAY).optional().description("댓글 목록"),
                                fieldWithPath("inquiries[].comments[].commentIdx").type(NUMBER).optional().description("댓글 ID"),
                                fieldWithPath("inquiries[].comments[].memberNickname").type(STRING).optional().description("회원 닉네임"),
                                fieldWithPath("inquiries[].comments[].managerNickname").type(STRING).optional().description("매니저 닉네임"),
                                fieldWithPath("inquiries[].comments[].regDate").type(STRING).optional().description("댓글 등록일"),
                                fieldWithPath("inquiries[].comments[].content").type(STRING).optional().description("댓글 내용"),
                                fieldWithPath("inquiries[].comments[].postIdx").type(NUMBER).optional().description("원글의 ID"),

                                fieldWithPath("page.blockPerPage").type(NUMBER).description("블록당 페이지 수"),
                                fieldWithPath("page.recordsPerPage").type(NUMBER).description("페이지당 레코드 수"),
                                fieldWithPath("page.pageNo").type(NUMBER).description("페이지 번호"),
                                fieldWithPath("page.recordStartIndex").type(NUMBER).description("레코드 시작 인덱스"),
                                fieldWithPath("page.maxPage").type(NUMBER).description("최대 페이지 수"),
                                fieldWithPath("page.startPage").type(NUMBER).description("시작 페이지"),
                                fieldWithPath("page.endPage").type(NUMBER).description("종료 페이지")
                        )));
    }

    @Test
    @DisplayName("공개 문의 게시글 응답")
    void getPublicInquiry() throws Exception {
        // given
        Long postIdx = 1L;
        given(inquiryIntegratedService.getPublicInquiryAndIncrementViewCount(postIdx))
                .willReturn(createNonSecretInquiry(
                        1L, InquiryFixture.SAMPLE_NICKNAME_1, InquiryFixture.SAMPLE_TITLE_1, InquiryFixture.SAMPLE_CONTENT_1));

        // when, then
        ResultActions resultActions = mockMvc.perform(RestDocumentationRequestBuilders.get("/api/inquiries/{postIdx}", postIdx)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        resultActions
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("postIdx").description("게시글 ID")
                        ),
                        responseFields(
                                fieldWithPath("postGroup").type(STRING).description("게시물 그룹 코드"),
                                fieldWithPath("postIdx").type(NUMBER).description("게시글 ID"),
                                fieldWithPath("title").type(STRING).description("제목"),
                                fieldWithPath("memberNickname").type(STRING).description("회원 닉네임"),
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
                                fieldWithPath("answerRegDate").type(STRING).description("답변 작성일(비밀글 설정 시 null 가능)").optional(),
                                fieldWithPath("inquiryStatus").type(STRING).description("답변 상태"),

                                fieldWithPath("comments").type(ARRAY).optional().description("댓글 목록"),
                                fieldWithPath("comments[].commentIdx").type(NUMBER).optional().description("댓글 ID"),
                                fieldWithPath("comments[].memberNickname").type(STRING).optional().description("회원 ID"),
                                fieldWithPath("comments[].managerNickname").type(STRING).optional().description("매니저 닉네임"),
                                fieldWithPath("comments[].regDate").type(STRING).optional().description("댓글 등록일"),
                                fieldWithPath("comments[].content").type(STRING).optional().description("댓글 내용"),
                                fieldWithPath("comments[].postIdx").type(NUMBER).optional().description("원글의 ID")
                        )));
    }

    @Test
    @DisplayName("비공개 비밀글 설정된 문의 게시글 응답")
    void getPrivateInquiry() throws Exception {
        // given
        Long postIdx = 1L;
        String password = "password1!";
        String accessToken = "AccessToken";

        given(inquiryIntegratedService.getPrivateInquiryAndIncrementViewCount(postIdx, accessToken, password))
                .willReturn(createSecretInquiry(
                        1L, InquiryFixture.SAMPLE_NICKNAME_1, InquiryFixture.SAMPLE_TITLE_1, InquiryFixture.SAMPLE_CONTENT_1));

        // when, then
        ResultActions resultActions = mockMvc.perform(RestDocumentationRequestBuilders.post("/api/inquiries/secret/{postIdx}", postIdx)
                        .cookie(new Cookie(JwtTokenProvider.ACCESS_TOKEN_KEY, accessToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("password", password))
                .andExpect(status().isOk());

        resultActions
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("postIdx").description("게시글 ID")
                        ),
                        customRequestHeaderCookies(
                                cookieWithName(JwtTokenProvider.ACCESS_TOKEN_KEY).description("액세스 토큰")
                        ),
                        requestParameters(
                                parameterWithName("password").description("비밀글 설정 비밀번호").optional()
                        ),
                        responseFields(
                                fieldWithPath("postGroup").ignored(),
                                fieldWithPath("postIdx").type(NUMBER).description("게시글 ID"),
                                fieldWithPath("title").type(STRING).description("제목"),
                                fieldWithPath("memberNickname").type(STRING).description("작성자 닉네임"),
                                fieldWithPath("regDate").type(STRING).description("등록일"),
                                fieldWithPath("modDate").type(STRING).description("수정일(null 가능)").optional(),
                                fieldWithPath("content").type(STRING).description("내용"),
                                fieldWithPath("viewCount").type(NUMBER).description("조회수"),
                                fieldWithPath("likeCount").type(NUMBER).description("좋아요 수"),
                                fieldWithPath("dislikeCount").type(NUMBER).description("싫어요 수"),
                                fieldWithPath("category").type(STRING).description("카테고리"),
                                fieldWithPath("secret").type(BOOLEAN).description("비밀글 설정 여부"),
                                fieldWithPath("answerManagerNickname").type(STRING).description("답변한 매니저 닉네임(비밀글 설정 시 null 가능)").optional(),
                                fieldWithPath("answerContent").type(STRING).description("답변 내용(비밀글 설정 시 null 가능)").optional(),
                                fieldWithPath("answerRegDate").type(STRING).description("답변 작성일(비밀글 설정 시 null 가능)").optional(),
                                fieldWithPath("inquiryStatus").type(STRING).description("답변 상태")
                        )));
    }

    @Test
    @DisplayName("문의 게시글 등록")
    void registerInquiry() throws Exception {
        String memberId = "member1";
        String accessToken = "Bearer accessToken";
        Inquiry inquiry = createInquiryForSave().updatePostIdx(1L);
        given(jwtTokenProvider.getClaim(accessToken, "id")).willReturn(memberId);
        given(jwtTokenProvider.getClaim(accessToken, "role")).willReturn(Authority.MEMBER.getValue());

        MockMultipartFile inquiryPart = new MockMultipartFile("inquiry", "",
                MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsBytes(inquiry));

        ResultActions resultActions =
                mockMvc.perform(RestDocumentationRequestBuilders.multipart("/api/inquiries")
                                .file(inquiryPart)
                                .cookie(new Cookie(JwtTokenProvider.ACCESS_TOKEN_KEY, accessToken))
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(MockMvcResultMatchers.header().exists(HttpHeaders.LOCATION))
                        .andExpect(MockMvcResultMatchers.status().isCreated());

        resultActions
                .andDo(restDocs.document(
                        customRequestHeaderCookies(
                                cookieWithName(JwtTokenProvider.ACCESS_TOKEN_KEY).description("액세스 토큰")
                        ),
                        requestParts(
                                partWithName("inquiry").description("문의 게시글 정보")
                        ),
                        requestPartFields("inquiry",
                                fieldWithPath("postIdx").ignored(),
                                fieldWithPath("postGroup").ignored(),
                                fieldWithPath("category").description("카테고리"),
                                fieldWithPath("title").description("제목").attributes(field("constraints", "길이 100 이하")),
                                fieldWithPath("content").description("내용").attributes(field("constraints", "길이 2000 이하")),
                                fieldWithPath("secret").description("비밀글 설정 여부"),
                                fieldWithPath("password").type(STRING).description("비밀글 설정시 비밀번호").optional(),
                                fieldWithPath("inquiryStatus").type(STRING).ignored()
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
        given(jwtTokenProvider.getClaim(accessToken, "id")).willReturn(memberId);
        given(jwtTokenProvider.getClaim(accessToken, "role")).willReturn(Authority.MEMBER.getValue());

        Inquiry updatedInquiry = Inquiry.builder()
                .category(Category.SERVICE)
                .title("수정된 제목")
                .content("수정된 내용")
                .secret(true)
                .password("qusrud132!")
                .build();

        MockMultipartFile inquiryPart = new MockMultipartFile("inquiry", "",
                MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsBytes(updatedInquiry));

        MockMultipartFile updateOptionPart = new MockMultipartFile("privacyUpdateOption", "",
                MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsBytes(PrivacyUpdateOption.CHANGE_PASSWORD));

        ResultActions resultActions =
                mockMvc.perform(RestDocumentationRequestBuilders.multipart("/api/inquiries/{postIdx}", postIdx)
                                .file(inquiryPart)
                                .file(updateOptionPart)
                                .cookie(new Cookie(JwtTokenProvider.ACCESS_TOKEN_KEY, accessToken))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(createInquiryForSave().updatePostIdx(postIdx)))
                                .with(request -> {
                                    request.setMethod(String.valueOf(HttpMethod.PUT)); // PUT 메서드로 변경
                                    return request;
                                }))
                        .andExpect(MockMvcResultMatchers.header().exists(HttpHeaders.LOCATION))
                        .andExpect(MockMvcResultMatchers.status().isCreated());

        resultActions
                .andDo(restDocs.document(
                        customRequestHeaderCookies(
                                cookieWithName(JwtTokenProvider.ACCESS_TOKEN_KEY).description("액세스 토큰")
                        ),
                        requestParts(
                                partWithName("inquiry").description("문의 게시글 정보"),
                                partWithName("privacyUpdateOption").description("문의 게시글 정보")
                        ),
                        requestPartFields("inquiry",
                                fieldWithPath("postGroup").ignored(),
                                fieldWithPath("category").description("카테고리"),
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

        given(jwtTokenProvider.getClaim(accessToken, "id")).willReturn(memberId);
        given(jwtTokenProvider.getClaim(accessToken, "role")).willReturn(Authority.MEMBER.getValue());

        ResultActions resultActions = mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/inquiries/{postIdx}", postIdx)
                        .cookie(new Cookie(JwtTokenProvider.ACCESS_TOKEN_KEY, accessToken)))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        resultActions
                .andDo(restDocs.document(
                        customRequestHeaderCookies(
                                cookieWithName(JwtTokenProvider.ACCESS_TOKEN_KEY).description("액세스 토큰")
                        ),
                        pathParameters(
                                parameterWithName("postIdx").description("삭제할 게시글 번호")
                        )
                ));
    }
}