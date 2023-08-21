package com.now.core.post.common.presentation;

import com.now.config.document.utils.RestDocsTestSupport;
import com.now.config.fixtures.member.MemberFixture;
import com.now.config.fixtures.post.InquiryFixture;
import com.now.config.fixtures.post.NoticeFixture;
import com.now.config.fixtures.post.PhotoFixture;
import com.now.config.fixtures.post.dto.PostReactionFixture;
import com.now.core.authentication.application.JwtTokenService;
import com.now.core.post.common.presentation.dto.Condition;
import com.now.core.post.common.presentation.dto.PostReaction;
import com.now.core.post.common.presentation.dto.Posts;
import com.now.core.post.common.presentation.dto.constants.Reaction;
import com.now.core.post.common.presentation.dto.constants.Sort;
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
import static com.now.config.fixtures.attachment.AttachmentFixture.createAttachments;
import static com.now.config.fixtures.comment.CommentFixture.createCommentForSave;
import static com.now.config.fixtures.comment.CommentFixture.createComments;
import static com.now.config.fixtures.post.CommunityFixture.*;
import static com.now.config.fixtures.post.InquiryFixture.createNonSecretInquiry;
import static com.now.config.fixtures.post.NoticeFixture.createNotice;
import static com.now.config.fixtures.post.PhotoFixture.createPhoto;
import static com.now.config.fixtures.post.dto.ConditionFixture.createCondition;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("게시글 컨트롤러는")
class PostControllerTest extends RestDocsTestSupport {

    @Test
    @DisplayName("모든 게시글 게시글 조회")
    void getAllPosts() throws Exception {
        // given
        Condition condition = createCondition(Sort.LATEST, 2);

        List<Posts> posts = Arrays.asList(
                new Posts(createNotice(1L, NoticeFixture.SAMPLE_NICKNAME_1, NoticeFixture.SAMPLE_TITLE_1, NoticeFixture.SAMPLE_CONTENT_1, createComments()), null, null, null),
                new Posts(createNotice(2L, NoticeFixture.SAMPLE_NICKNAME_2, NoticeFixture.SAMPLE_TITLE_2, NoticeFixture.SAMPLE_CONTENT_2, createComments()), null, null, null),

                new Posts(null, createCommunity(3L, SAMPLE_NICKNAME_1, SAMPLE_TITLE_1, SAMPLE_CONTENT_1, createAttachments(), createComments()), null, null),
                new Posts(null, createCommunity(4L, SAMPLE_NICKNAME_1, SAMPLE_TITLE_1, SAMPLE_CONTENT_1, createAttachments(), createComments()), null, null),

                new Posts(null, null, createPhoto(5L, SAMPLE_NICKNAME_1, PhotoFixture.SAMPLE_TITLE_1, PhotoFixture.SAMPLE_CONTENT_1, createAttachments(), createComments()), null),
                new Posts(null, null, createPhoto(6L, SAMPLE_NICKNAME_1, PhotoFixture.SAMPLE_TITLE_1, PhotoFixture.SAMPLE_CONTENT_1, createAttachments(), createComments()), null),

                new Posts(null, null, null, createNonSecretInquiry(7L, InquiryFixture.SAMPLE_NICKNAME_1, InquiryFixture.SAMPLE_TITLE_1, InquiryFixture.SAMPLE_CONTENT_1)),
                new Posts(null, null, null, createNonSecretInquiry(8L, InquiryFixture.SAMPLE_NICKNAME_1, InquiryFixture.SAMPLE_TITLE_1, InquiryFixture.SAMPLE_CONTENT_1))
        );
        given(postService.getAllPosts(condition)).willReturn(posts);

        // when, then
        ResultActions resultActions =
                mockMvc.perform(RestDocumentationRequestBuilders.get("/api/posts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("sort", condition.getSort().name())
                                .param("maxNumberOfPosts", String.valueOf(condition.getMaxNumberOfPosts())))
                        .andExpect(status().isOk());

        // then
        resultActions
                .andDo(restDocs.document(
                        requestParameters(
                                parameterWithName("sort").description("정렬"),
                                parameterWithName("maxNumberOfPosts").description("페이지 개수 제한")
                        ),
                        responseFields(
                                fieldWithPath("notices[]").type(ARRAY).description("공지 게시글 목록"),
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

                                fieldWithPath("communities[]").type(ARRAY).description("커뮤니티 게시글 목록"),
                                fieldWithPath("communities[].postGroup").type(STRING).description("게시물 그룹 코드"),
                                fieldWithPath("communities[].postIdx").type(NUMBER).description("게시글 ID"),
                                fieldWithPath("communities[].title").type(STRING).description("제목"),
                                fieldWithPath("communities[].memberNickname").type(STRING).description("회원 닉네임"),
                                fieldWithPath("communities[].regDate").type(STRING).description("등록일"),
                                fieldWithPath("communities[].modDate").type(STRING).optional().description("수정일(null 가능)"),
                                fieldWithPath("communities[].content").type(STRING).description("내용"),
                                fieldWithPath("communities[].viewCount").type(NUMBER).description("조회수"),
                                fieldWithPath("communities[].likeCount").type(NUMBER).description("좋아요 수"),
                                fieldWithPath("communities[].dislikeCount").type(NUMBER).description("싫어요 수"),
                                fieldWithPath("communities[].category").type(STRING).description("카테고리"),

                                fieldWithPath("communities[].attachments").type(ARRAY).optional().description("첨부파일 목록"),
                                fieldWithPath("communities[].attachments[].attachmentIdx").type(NUMBER).optional().description("첨부파일 ID"),
                                fieldWithPath("communities[].attachments[].originalAttachmentName").type(STRING).optional().description("원본 첨부파일 이름"),
                                fieldWithPath("communities[].attachments[].savedAttachmentName").type(STRING).optional().description("저장된 첨부파일 이름"),
                                fieldWithPath("communities[].attachments[].attachmentExtension").type(STRING).optional().description("첨부파일 확장자"),
                                fieldWithPath("communities[].attachments[].attachmentSize").type(NUMBER).optional().description("첨부파일 크기"),
                                fieldWithPath("communities[].attachments[].postIdx").type(NUMBER).optional().description("원글의 ID"),

                                fieldWithPath("communities[].comments").type(ARRAY).optional().description("댓글 목록"),
                                fieldWithPath("communities[].comments[].commentIdx").type(NUMBER).optional().description("댓글 ID"),
                                fieldWithPath("communities[].comments[].memberNickname").type(STRING).optional().description("회원 닉네임"),
                                fieldWithPath("communities[].comments[].managerNickname").type(STRING).optional().description("매니저 닉네임"),
                                fieldWithPath("communities[].comments[].regDate").type(STRING).optional().description("댓글 등록일"),
                                fieldWithPath("communities[].comments[].content").type(STRING).optional().description("댓글 내용"),
                                fieldWithPath("communities[].comments[].postIdx").type(NUMBER).optional().description("원글의 ID"),

                                fieldWithPath("photos[]").type(ARRAY).description("사진 게시글 목록"),
                                fieldWithPath("photos[].postGroup").type(STRING).description("게시물 그룹 코드"),
                                fieldWithPath("photos[].postIdx").type(NUMBER).description("게시글 ID"),
                                fieldWithPath("photos[].title").type(STRING).description("제목"),
                                fieldWithPath("photos[].memberNickname").type(STRING).description("회원 닉네임"),
                                fieldWithPath("photos[].regDate").type(STRING).description("등록일"),
                                fieldWithPath("photos[].modDate").type(STRING).optional().description("수정일(null 가능)"),
                                fieldWithPath("photos[].content").type(STRING).description("내용"),
                                fieldWithPath("photos[].viewCount").type(NUMBER).description("조회수"),
                                fieldWithPath("photos[].likeCount").type(NUMBER).description("좋아요 수"),
                                fieldWithPath("photos[].dislikeCount").type(NUMBER).description("싫어요 수"),
                                fieldWithPath("photos[].category").type(STRING).description("카테고리"),
                                fieldWithPath("photos[].thumbnailAttachmentIdx").type(NUMBER).description("대표 이미지로 설정된 첨부파일 ID"),

                                fieldWithPath("photos[].attachments").type(ARRAY).optional().description("첨부파일 목록"),
                                fieldWithPath("photos[].attachments[].attachmentIdx").type(NUMBER).optional().description("첨부파일 ID"),
                                fieldWithPath("photos[].attachments[].originalAttachmentName").type(STRING).optional().description("원본 첨부파일 이름"),
                                fieldWithPath("photos[].attachments[].savedAttachmentName").type(STRING).optional().description("저장된 첨부파일 이름"),
                                fieldWithPath("photos[].attachments[].attachmentExtension").type(STRING).optional().description("첨부파일 확장자"),
                                fieldWithPath("photos[].attachments[].attachmentSize").type(NUMBER).optional().description("첨부파일 크기"),
                                fieldWithPath("photos[].attachments[].postIdx").type(NUMBER).optional().description("원글의 ID"),

                                fieldWithPath("photos[].comments").type(ARRAY).optional().description("댓글 목록"),
                                fieldWithPath("photos[].comments[].commentIdx").type(NUMBER).optional().description("댓글 ID"),
                                fieldWithPath("photos[].comments[].memberNickname").type(STRING).optional().description("회원 닉네임"),
                                fieldWithPath("photos[].comments[].managerNickname").type(STRING).optional().description("매니저 닉네임"),
                                fieldWithPath("photos[].comments[].regDate").type(STRING).optional().description("댓글 등록일"),
                                fieldWithPath("photos[].comments[].content").type(STRING).optional().description("댓글 내용"),
                                fieldWithPath("photos[].comments[].postIdx").type(NUMBER).optional().description("원글의 ID"),

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
                                fieldWithPath("inquiries[].comments[].postIdx").type(NUMBER).optional().description("원글의 ID")
                        )));
    }

    @Test
    @DisplayName("게시글 반응 저장")
    void updateReaction() throws Exception {
        Long postIdx = 1L;
        String memberId = MemberFixture.MEMBER1_ID;
        String accessToken = "Bearer accessToken";
        PostReaction postReaction = PostReactionFixture.createPostReaction(Reaction.DISLIKE);

        given(jwtTokenService.getClaim(accessToken, "id")).willReturn(memberId);
        ResultActions resultActions =
                mockMvc.perform(RestDocumentationRequestBuilders.post("/api/posts/{postIdx}/reaction", postIdx)
                                .contentType(MediaType.APPLICATION_JSON)
                                .cookie(new Cookie(JwtTokenService.ACCESS_TOKEN_KEY, accessToken))
                                .content(objectMapper.writeValueAsString(postReaction)))
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
                                fieldWithPath("reaction").description("게시글 반응")
                        )
                ));
    }
}