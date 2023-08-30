package com.now.core.post.notice.presentation;

import com.now.config.document.utils.RestDocsTestSupport;
import com.now.config.fixtures.post.NoticeFixture;
import com.now.core.authentication.application.JwtTokenService;
import com.now.core.authentication.constants.Authority;
import com.now.core.category.domain.constants.Category;
import com.now.core.post.common.presentation.dto.Condition;
import com.now.core.post.notice.domain.Notice;
import com.now.core.post.notice.presentation.dto.NoticesResponse;
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
import static com.now.config.fixtures.comment.CommentFixture.createComments;
import static com.now.config.fixtures.post.NoticeFixture.createNotice;
import static com.now.config.fixtures.post.NoticeFixture.createNoticeForSave;
import static com.now.config.fixtures.post.dto.ConditionFixture.createCondition;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("공지 컨트롤러는")
class NoticeControllerTest extends RestDocsTestSupport {

    @Test
    @DisplayName("모든 공지 게시글 조회")
    void getAllNotices() throws Exception {
        // given
        Condition condition = createCondition(Category.NEWS).updatePage();

        NoticesResponse noticesResponse = NoticesResponse.builder()
                .notices(List.of(
                        createNotice(1L, NoticeFixture.SAMPLE_NICKNAME_1, NoticeFixture.SAMPLE_TITLE_1, NoticeFixture.SAMPLE_CONTENT_1, createComments()),
                        createNotice(2L, NoticeFixture.SAMPLE_NICKNAME_2, NoticeFixture.SAMPLE_TITLE_2, NoticeFixture.SAMPLE_CONTENT_2, createComments())
                ))
                .page(createCondition(Category.COMMUNITY_STUDY).updatePage().getPage().calculatePageInfo(2L))
                .build();

        given(noticeIntegratedService.getAllNoticesWithPageInfo(condition)).willReturn(noticesResponse);

        // when, then
        ResultActions resultActions =
                mockMvc.perform(RestDocumentationRequestBuilders.get("/api/notices")
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
                                fieldWithPath("notices[]").type(ARRAY).description("공지 목록"),
                                fieldWithPath("notices[].postGroup").type(STRING).description("게시물 그룹 코드"),
                                fieldWithPath("notices[].postIdx").type(NUMBER).description("게시글 ID"),
                                fieldWithPath("notices[].title").type(STRING).description("제목"),
                                fieldWithPath("notices[].managerNickname").type(STRING).description("매니저 닉네임"),
                                fieldWithPath("notices[].regDate").type(STRING).description("등록일"),
                                fieldWithPath("notices[].modDate").type(STRING).optional().description("수정일(null 가능)"),
                                fieldWithPath("notices[].content").type(STRING).description("내용"),
                                fieldWithPath("notices[].viewCount").type(NUMBER).description("조회수"),
                                fieldWithPath("notices[].likeCount").type(NUMBER).description("좋아요 수"),
                                fieldWithPath("notices[].dislikeCount").type(NUMBER).description("싫어요 수"),
                                fieldWithPath("notices[].category").type(STRING).description("카테고리"),
                                fieldWithPath("notices[].pinned").type(BOOLEAN).description("상단 고정 여부"),

                                fieldWithPath("notices[].comments").type(ARRAY).optional().description("댓글 목록"),
                                fieldWithPath("notices[].comments[].commentIdx").type(NUMBER).optional().description("댓글 ID"),
                                fieldWithPath("notices[].comments[].memberNickname").type(STRING).optional().description("회원 닉네임"),
                                fieldWithPath("notices[].comments[].managerNickname").type(STRING).optional().description("매니저 닉네임"),
                                fieldWithPath("notices[].comments[].regDate").type(STRING).optional().description("댓글 등록일"),
                                fieldWithPath("notices[].comments[].content").type(STRING).optional().description("댓글 내용"),
                                fieldWithPath("notices[].comments[].postIdx").type(NUMBER).optional().description("원글의 ID"),

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
    @DisplayName("공지 게시글 응답")
    void getNotice() throws Exception {
        // given
        Long postIdx = 1L;
        given(noticeIntegratedService.getNoticeAndIncrementViewCount(postIdx))
                .willReturn(createNotice(2L, NoticeFixture.SAMPLE_NICKNAME_2, NoticeFixture.SAMPLE_TITLE_2, NoticeFixture.SAMPLE_CONTENT_2, createComments()));

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
                                fieldWithPath("postGroup").type(STRING).description("게시물 그룹 코드"),
                                fieldWithPath("title").type(STRING).description("제목"),
                                fieldWithPath("managerNickname").type(STRING).description("작성자 닉네임"),
                                fieldWithPath("regDate").type(STRING).description("등록일"),
                                fieldWithPath("modDate").type(STRING).optional().description("수정일(null 가능)"),
                                fieldWithPath("content").type(STRING).description("내용"),
                                fieldWithPath("viewCount").type(NUMBER).description("조회수"),
                                fieldWithPath("likeCount").type(NUMBER).description("좋아요 수"),
                                fieldWithPath("dislikeCount").type(NUMBER).description("싫어요 수"),
                                fieldWithPath("category").type(STRING).description("카테고리"),
                                fieldWithPath("pinned").type(BOOLEAN).description("상단 고정 여부"),

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
    @DisplayName("공지 게시글 등록")
    void registerNotice() throws Exception {
        String managerId = "manager1";
        String accessToken = "Bearer accessToken";
        Notice notice = createNoticeForSave().updatePostIdx(1L);
        given(jwtTokenService.getClaim(accessToken, "id")).willReturn(managerId);
        given(jwtTokenService.getClaim(accessToken, "role")).willReturn(Authority.MANAGER.getValue());

        MockMultipartFile noticePart = new MockMultipartFile("notice", "",
                MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsBytes(notice));

        ResultActions resultActions =
                mockMvc.perform(RestDocumentationRequestBuilders.multipart("/api/manager/notices")
                                .file(noticePart)
                                .cookie(new Cookie(JwtTokenService.ACCESS_TOKEN_KEY, accessToken))
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(MockMvcResultMatchers.header().exists(HttpHeaders.LOCATION))
                        .andExpect(MockMvcResultMatchers.status().isCreated());

        resultActions
                .andDo(restDocs.document(
                        customRequestHeaderCookies(
                                cookieWithName(JwtTokenService.ACCESS_TOKEN_KEY).description("액세스 토큰")
                        ),
                        requestParts(
                                partWithName("notice").description("공지 게시글 정보")
                        ),
                        requestPartFields("notice",
                                fieldWithPath("postGroup").ignored(),
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

        Notice updatedNotice = Notice.builder()
                .category(Category.NEWS)
                .title("수정된 제목")
                .content("수정된 내용")
                .pinned(true)
                .build();

        MockMultipartFile noticePart = new MockMultipartFile("notice", "",
                MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsBytes(updatedNotice));

        ResultActions resultActions =
                mockMvc.perform(RestDocumentationRequestBuilders.multipart("/api/manager/notices/{postIdx}", postIdx)
                                .file(noticePart)
                                .cookie(new Cookie(JwtTokenService.ACCESS_TOKEN_KEY, accessToken))
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(request -> {
                                    request.setMethod(String.valueOf(HttpMethod.PUT)); // PUT 메서드로 변경
                                    return request;
                                }))
                        .andExpect(MockMvcResultMatchers.header().exists(HttpHeaders.LOCATION))
                        .andExpect(MockMvcResultMatchers.status().isCreated());

        resultActions
                .andDo(restDocs.document(
                        customRequestHeaderCookies(
                                cookieWithName(JwtTokenService.ACCESS_TOKEN_KEY).description("액세스 토큰")
                        ),
                        requestParts(
                                partWithName("notice").description("공지 게시글 정보")
                        ),
                        requestPartFields("notice",
                                fieldWithPath("postGroup").ignored(),
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
        String accessToken = "Bearer accessToken";

        given(jwtTokenService.getClaim(accessToken, "id")).willReturn(managerId);
        given(jwtTokenService.getClaim(accessToken, "role")).willReturn(Authority.MANAGER.getValue());

        ResultActions resultActions = mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/manager/notices/{postIdx}", postIdx)
                        .cookie(new Cookie(JwtTokenService.ACCESS_TOKEN_KEY, accessToken)))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        resultActions
                .andDo(restDocs.document(
                        customRequestHeaderCookies(
                                cookieWithName(JwtTokenService.ACCESS_TOKEN_KEY).description("액세스 토큰")
                        ),
                        pathParameters(
                                parameterWithName("postIdx").description("게시글 번호")
                        )
                ));
    }
}