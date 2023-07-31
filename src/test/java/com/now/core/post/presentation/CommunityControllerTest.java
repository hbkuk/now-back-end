package com.now.core.post.presentation;

import com.now.config.document.utils.RestDocsTestSupport;
import com.now.core.authentication.application.JwtTokenService;
import com.now.core.authentication.constants.Authority;
import com.now.core.category.domain.constants.Category;
import com.now.core.post.domain.Community;
import com.now.core.post.presentation.dto.Condition;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.servlet.http.Cookie;
import java.util.List;

import static com.now.common.snippet.RequestCookiesSnippet.cookieWithName;
import static com.now.common.snippet.RequestCookiesSnippet.customRequestHeaderCookies;
import static com.now.config.document.utils.RestDocsConfig.field;
import static com.now.config.fixtures.attachment.AttachmentFixture.createAttachments;
import static com.now.config.fixtures.comment.CommentFixture.createComments;
import static com.now.config.fixtures.post.CommunityFixture.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CommunityControllerTest extends RestDocsTestSupport {

    @Test
    @DisplayName("모든 커뮤니티 게시글 조회")
    void getAllCommunities() throws Exception {
        // given
        Condition condition = new Condition(5);
        given(communityService.getAllCommunities(condition))
                .willReturn(List.of(
                        createCommunity(1L, SAMPLE_NICKNAME_1, SAMPLE_TITLE_1, SAMPLE_CONTENT_1, createAttachments(), createComments()),
                        createCommunity(2L, SAMPLE_NICKNAME_2, SAMPLE_TITLE_2, SAMPLE_CONTENT_2, createAttachments(), createComments())
                ));

        // when, then
        ResultActions resultActions =
                mockMvc.perform(RestDocumentationRequestBuilders.get("/api/communities")
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
                                fieldWithPath("[]").type(ARRAY).description("커뮤니티 게시글 목록"),
                                fieldWithPath("[].postGroup").type(STRING).description("게시물 그룹 코드"),
                                fieldWithPath("[].postIdx").type(NUMBER).description("게시글 ID"),
                                fieldWithPath("[].title").type(STRING).description("제목"),
                                fieldWithPath("[].memberNickname").type(STRING).description("회원 닉네임"),
                                fieldWithPath("[].regDate").type(STRING).description("등록일"),
                                fieldWithPath("[].modDate").type(STRING).optional().description("수정일(null 가능)"),
                                fieldWithPath("[].content").type(STRING).description("내용"),
                                fieldWithPath("[].viewCount").type(NUMBER).description("조회수"),
                                fieldWithPath("[].likeCount").type(NUMBER).description("좋아요 수"),
                                fieldWithPath("[].dislikeCount").type(NUMBER).description("싫어요 수"),
                                fieldWithPath("[].category").type(STRING).description("카테고리"),

                                fieldWithPath("[].attachments").type(ARRAY).optional().description("첨부파일 목록"),
                                fieldWithPath("[].attachments[].attachmentIdx").type(NUMBER).optional().description("첨부파일 ID"),
                                fieldWithPath("[].attachments[].originalAttachmentName").type(STRING).optional().description("원본 첨부파일 이름"),
                                fieldWithPath("[].attachments[].savedAttachmentName").type(STRING).optional().description("저장된 첨부파일 이름"),
                                fieldWithPath("[].attachments[].attachmentExtension").type(STRING).optional().description("첨부파일 확장자"),
                                fieldWithPath("[].attachments[].attachmentSize").type(NUMBER).optional().description("첨부파일 크기"),
                                fieldWithPath("[].attachments[].postIdx").type(NUMBER).optional().description("원글의 ID"),

                                fieldWithPath("[].comments").type(ARRAY).optional().description("댓글 목록"),
                                fieldWithPath("[].comments[].commentIdx").type(NUMBER).optional().description("댓글 ID"),
                                fieldWithPath("[].comments[].memberNickname").type(STRING).optional().description("회원 닉네임"),
                                fieldWithPath("[].comments[].managerNickname").type(STRING).optional().description("매니저 닉네임"),
                                fieldWithPath("[].comments[].regDate").type(STRING).optional().description("댓글 등록일"),
                                fieldWithPath("[].comments[].content").type(STRING).optional().description("댓글 내용"),
                                fieldWithPath("[].comments[].postIdx").type(NUMBER).optional().description("원글의 ID")
                        )));
    }

    @Test
    @DisplayName("커뮤니티 게시글 응답")
    void getCommunity() throws Exception {
        // given
        Long postIdx = 1L;
        given(communityService.getCommunity(postIdx))
                .willReturn(createCommunity(
                        1L, SAMPLE_NICKNAME_1, SAMPLE_TITLE_1, SAMPLE_CONTENT_1, createAttachments(), createComments()));

        // when, then
        ResultActions resultActions = mockMvc.perform(RestDocumentationRequestBuilders.get("/api/communities/{postIdx}", postIdx)
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
                                fieldWithPath("memberNickname").type(STRING).description("회원 닉네임"),
                                fieldWithPath("regDate").type(STRING).description("등록일"),
                                fieldWithPath("modDate").type(STRING).optional().description("수정일(null 가능)"),
                                fieldWithPath("content").type(STRING).description("내용"),
                                fieldWithPath("viewCount").type(NUMBER).description("조회수"),
                                fieldWithPath("likeCount").type(NUMBER).description("좋아요 수"),
                                fieldWithPath("dislikeCount").type(NUMBER).description("싫어요 수"),
                                fieldWithPath("category").type(STRING).description("카테고리"),

                                fieldWithPath("attachments").type(ARRAY).optional().description("첨부파일 목록"),
                                fieldWithPath("attachments[].attachmentIdx").type(NUMBER).optional().description("첨부파일 ID"),
                                fieldWithPath("attachments[].originalAttachmentName").type(STRING).optional().description("원본 첨부파일 이름"),
                                fieldWithPath("attachments[].savedAttachmentName").type(STRING).optional().description("저장된 첨부파일 이름"),
                                fieldWithPath("attachments[].attachmentExtension").type(STRING).optional().description("첨부파일 확장자"),
                                fieldWithPath("attachments[].attachmentSize").type(NUMBER).optional().description("첨부파일 크기"),
                                fieldWithPath("attachments[].postIdx").type(NUMBER).optional().description("원글의 ID"),

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
    @DisplayName("커뮤니티 게시글 등록")
    void registerCommunity() throws Exception {
        String memberId = "tester1";
        String accessToken = "Bearer accessToken";
        Community community = createCommunityForSave().updatePostIdx(1L);
        given(jwtTokenService.getClaim(accessToken, "id")).willReturn(memberId);
        given(jwtTokenService.getClaim(accessToken, "role")).willReturn(Authority.MEMBER.getValue());

        MockMultipartFile communityPart = new MockMultipartFile("community", "",
                MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsBytes(community));

        MockMultipartFile fileA = new MockMultipartFile("attachment", "file1.png",
                MediaType.MULTIPART_FORM_DATA_VALUE, "file1 content".getBytes());

        MockMultipartFile fileB = new MockMultipartFile("attachment", "file2.png",
                MediaType.MULTIPART_FORM_DATA_VALUE, "file2 content".getBytes());

        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.multipart("/api/communities")
                        .file(communityPart)
                        .file(fileA)
                        .file(fileB)
                        .accept(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie(JwtTokenService.ACCESS_TOKEN_KEY, accessToken)))
                .andExpect(MockMvcResultMatchers.header().exists(HttpHeaders.LOCATION))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        resultActions
                .andDo(restDocs.document(
                        customRequestHeaderCookies(
                                cookieWithName(JwtTokenService.ACCESS_TOKEN_KEY).description("액세스 토큰")
                        ),
                        requestParts(
                                partWithName("community").description("커뮤니티 게시글 정보"),
                                partWithName("attachment").description("첨부파일 (다중 파일 업로드 가능)").optional()
                        ),
                        requestPartFields("community",
                                fieldWithPath("postGroup").ignored(),
                                fieldWithPath("postIdx").ignored(),
                                fieldWithPath("category").description("카테고리"),
                                fieldWithPath("title").description("제목").attributes(field("constraints", "길이 100 이하")),
                                fieldWithPath("content").description("내용").attributes(field("constraints", "길이 2000 이하"))
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("생성된 위치 URI")
                        )
                ));
    }

    @Test
    @DisplayName("커뮤니티 게시글 수정")
    void updateCommunity() throws Exception {
        Long postIdx = 1L;
        String memberId = "tester1";
        String accessToken = "Bearer accessToken";
        given(jwtTokenService.getClaim(accessToken, "id")).willReturn(memberId);
        given(jwtTokenService.getClaim(accessToken, "role")).willReturn(Authority.MEMBER.getValue());

        Community updatedCommunity = Community.builder()
                .category(Category.COMMUNITY_STUDY)
                .title("수정된 제목")
                .content("수정된 내용")
                .build();

        MockMultipartFile communityPart = new MockMultipartFile("community", "",
                MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsBytes(updatedCommunity));

        MockMultipartFile fileA = new MockMultipartFile("attachment", "file1.png",
                MediaType.MULTIPART_FORM_DATA_VALUE, "file1 content".getBytes());

        MockMultipartFile fileB = new MockMultipartFile("attachment", "file2.png",
                MediaType.MULTIPART_FORM_DATA_VALUE, "file2 content".getBytes());

        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.multipart("/api/communities/{postIdx}", postIdx)
                        .file(communityPart)
                        .file(fileA)
                        .file(fileB)
                        .param("attachmentIdx", "1")
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                        .cookie(new Cookie(JwtTokenService.ACCESS_TOKEN_KEY, accessToken))
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
                                partWithName("community").description("커뮤니티 게시글 정보"),
                                partWithName("attachment").description("첨부파일 (다중 파일 업로드 가능)").optional()
                        ),
                        requestParameters(
                                parameterWithName("attachmentIdx").description("이전에 업로드된 첨부파일 ID").optional()
                        ),
                        requestPartFields("community",
                                fieldWithPath("postGroup").ignored(),
                                fieldWithPath("category").description("카테고리"),
                                fieldWithPath("title").description("제목").attributes(field("constraints", "길이 100 이하")),
                                fieldWithPath("content").description("내용").attributes(field("constraints", "길이 2000 이하"))
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("생성된 위치 URI")
                        )
                ));
    }

    @Test
    @DisplayName("커뮤니티 게시글 삭제")
    void deleteCommunity() throws Exception {
        Long postIdx = 1L;
        String memberId = "tester1";
        String accessToken = "Bearer accessToken";
        given(jwtTokenService.getClaim(accessToken, "id")).willReturn(memberId);
        given(jwtTokenService.getClaim(accessToken, "role")).willReturn(Authority.MEMBER.getValue());

        ResultActions resultActions = mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/communities/{postIdx}", postIdx)
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