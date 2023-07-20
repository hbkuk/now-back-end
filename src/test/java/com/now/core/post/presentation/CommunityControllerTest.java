package com.now.core.post.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.now.core.attachment.application.AttachmentService;
import com.now.core.authentication.application.JwtTokenService;
import com.now.core.authentication.constants.Authority;
import com.now.core.comment.application.CommentService;
import com.now.core.post.application.CommunityService;
import com.now.core.post.domain.Community;
import com.now.core.post.presentation.dto.Condition;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.AutoConfigureMybatis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.operation.preprocess.OperationRequestPreprocessor;
import org.springframework.restdocs.operation.preprocess.OperationResponsePreprocessor;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static com.now.core.attachment.domain.AttachmentTest.createAttachments;
import static com.now.core.comment.CommentTest.createComments;
import static com.now.core.post.domain.CommunityTest.createCommunity;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommunityController.class)
@AutoConfigureMybatis
@AutoConfigureRestDocs
class CommunityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    private CommunityService communityService;

    @MockBean
    private AttachmentService attachmentService;

    @MockBean
    private CommentService commentService;

    @MockBean
    private JwtTokenService jwtTokenService;

    protected OperationResponsePreprocessor getResponsePreprocessor() {
        return Preprocessors.preprocessResponse(prettyPrint());
    }

    protected OperationRequestPreprocessor getRequestPreprocessor() {
        return Preprocessors.preprocessRequest(prettyPrint());
    }

    @Test
    @DisplayName("모든 커뮤니티 게시글 정보를 조회")
    void getAllCommunities() throws Exception {
        // given
        Condition condition = new Condition(5);
        given(communityService.getAllCommunities(condition))
                .willReturn(List.of(
                        createCommunity(createAttachments(), createComments()),
                        createCommunity(createAttachments(), createComments()),
                        createCommunity(createAttachments(), createComments())
                ));

        // when, then
        ResultActions resultActions = mockMvc.perform(RestDocumentationRequestBuilders.get("/api/communities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("maxNumberOfPosts", String.valueOf(condition.getMaxNumberOfPosts())))
                        .andExpect(status().isOk());

        resultActions
                .andDo(document("community/communities-get",
                        getResponsePreprocessor(),
                responseFields(
                        fieldWithPath("[]").type(ARRAY).description("커뮤니티 게시글 목록"),
                        fieldWithPath("[].postIdx").type(NUMBER).description("게시글 ID"),
                        fieldWithPath("[].title").type(STRING).description("제목"),
                        fieldWithPath("[].memberNickname").type(STRING).description("작성자 닉네임"),
                        fieldWithPath("[].regDate").type(STRING).description("등록일"),
                        fieldWithPath("[].modDate").type(STRING).description("수정일"),
                        fieldWithPath("[].content").type(STRING).description("내용"),
                        fieldWithPath("[].viewCount").type(NUMBER).description("조회수"),
                        fieldWithPath("[].likeCount").type(NUMBER).description("좋아요 수"),
                        fieldWithPath("[].dislikeCount").type(NUMBER).description("싫어요 수"),
                        fieldWithPath("[].category").type(STRING).description("카테고리"),
                        fieldWithPath("[].attachments").type(ARRAY).description("첨부파일 목록"),
                        fieldWithPath("[].attachments[].attachmentIdx").type(NUMBER).description("첨부파일 ID"),
                        fieldWithPath("[].attachments[].originalAttachmentName").type(STRING).description("원본 첨부파일 이름"),
                        fieldWithPath("[].attachments[].attachmentExtension").type(STRING).description("첨부파일 확장자"),
                        fieldWithPath("[].attachments[].attachmentSize").type(NUMBER).description("첨부파일 크기"),
                        fieldWithPath("[].attachments[].memberPostIdx").type(NUMBER).description("게시글 ID (attachments와 조인 필드)"),
                        fieldWithPath("[].comments").type(ARRAY).description("댓글 목록"),
                        fieldWithPath("[].comments[].commentIdx").type(NUMBER).optional().description("댓글 ID (null 가능)"),
                        fieldWithPath("[].comments[].memberIdx").type(STRING).description("댓글 작성자 ID"),
                        fieldWithPath("[].comments[].regDate").type(STRING).optional().description("댓글 등록일 (null 가능)"),
                        fieldWithPath("[].comments[].content").type(STRING).description("댓글 내용"),
                        fieldWithPath("[].comments[].memberPostIdx").type(NUMBER).description("게시글 ID (comments와 조인 필드)")
                )));
    }

    @Test
    @DisplayName("커뮤니티 게시글 응답")
    void getCommunity() throws Exception {
        // given
        Long postIdx = 1L;
        Community community = createCommunity(createAttachments(), createComments());
        given(communityService.getCommunity(postIdx)).willReturn(community);

        // when, then
        ResultActions resultActions = mockMvc.perform(RestDocumentationRequestBuilders.get("/api/community/{postIdx}", postIdx)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        resultActions
                .andDo(document("community/community-get",
                        getResponsePreprocessor(),
                        pathParameters(
                                parameterWithName("postIdx").description("게시글 ID")
                        ),
                        responseFields(
                                fieldWithPath("postIdx").type(NUMBER).description("게시글 ID"),
                                fieldWithPath("title").type(STRING).description("제목"),
                                fieldWithPath("memberNickname").type(STRING).description("작성자 닉네임"),
                                fieldWithPath("regDate").type(STRING).description("등록일"),
                                fieldWithPath("modDate").type(STRING).description("수정일"),
                                fieldWithPath("content").type(STRING).description("내용"),
                                fieldWithPath("viewCount").type(NUMBER).description("조회수"),
                                fieldWithPath("likeCount").type(NUMBER).description("좋아요 수"),
                                fieldWithPath("dislikeCount").type(NUMBER).description("싫어요 수"),
                                fieldWithPath("category").type(STRING).description("카테고리"),
                                fieldWithPath("attachments[].attachmentIdx").type(NUMBER).description("첨부파일 ID"),
                                fieldWithPath("attachments[].originalAttachmentName").type(STRING).description("원본 첨부파일 이름"),
                                fieldWithPath("attachments[].attachmentExtension").type(STRING).description("첨부파일 확장자"),
                                fieldWithPath("attachments[].attachmentSize").type(NUMBER).description("첨부파일 크기"),
                                fieldWithPath("attachments[].memberPostIdx").type(NUMBER).description("게시글 ID (attachments와 조인 필드)"),
                                fieldWithPath("comments[].commentIdx").type(NUMBER).optional().description("댓글 ID (null 가능)"),
                                fieldWithPath("comments[].memberIdx").type(STRING).description("댓글 작성자 ID"),
                                fieldWithPath("comments[].regDate").type(STRING).optional().description("댓글 등록일 (null 가능)"),
                                fieldWithPath("comments[].content").type(STRING).description("댓글 내용"),
                                fieldWithPath("comments[].memberPostIdx").type(NUMBER).description("게시글 ID (comments와 조인 필드)")
                        )));
    }



    @Test
    @DisplayName("커뮤니티 게시글 등록")
    void registerCommunity() throws Exception {
        String memberId = "tester1";
        String token = "Bearer accessToken";
        given(jwtTokenService.getClaim(token, "id")).willReturn(memberId);
        given(jwtTokenService.getClaim(token, "role")).willReturn(Authority.MEMBER.getValue());

        Community community = createCommunity(memberId);

        MockMultipartFile communityPart = new MockMultipartFile("community", "",
                MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsBytes(community));

        MockMultipartFile fileA = new MockMultipartFile("attachment", "file1.png",
                MediaType.TEXT_PLAIN_VALUE, "file1 content".getBytes());

        MockMultipartFile fileB = new MockMultipartFile("attachment", "file2.png",
                MediaType.TEXT_PLAIN_VALUE, "file2 content".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/community")
                        .file(communityPart)
                        .file(fileA)
                        .file(fileB)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    @DisplayName("커뮤니티 게시글 수정")
    void updateCommunity() throws Exception {
        String memberId = "tester1";
        String token = "Bearer accessToken";
        Long postIdx = 1L;

        given(jwtTokenService.getClaim(token, "id")).willReturn(memberId);
        given(jwtTokenService.getClaim(token, "role")).willReturn(Authority.MEMBER.getValue());

        Community updatedCommunity = createCommunity(memberId);

        MockMultipartFile communityPart = new MockMultipartFile("community", "",
                MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsBytes(updatedCommunity));

        MockMultipartFile fileA = new MockMultipartFile("attachment", "file1.png",
                MediaType.TEXT_PLAIN_VALUE, "file1 content".getBytes());

        MockMultipartFile fileB = new MockMultipartFile("attachment", "file2.png",
                MediaType.TEXT_PLAIN_VALUE, "file2 content".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/community/" + postIdx)
                        .file(communityPart)
                        .file(fileA)
                        .file(fileB)
                        .param("attachmentIdx", "1,2") // 이전에 업로드한 파일 인덱스 전달
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .with(request -> {
                            request.setMethod("PUT"); // PUT 메서드로 변경
                            return request;
                        }))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    @DisplayName("커뮤니티 게시글 삭제")
    void deleteCommunity() throws Exception {
        String memberId = "tester1";
        String token = "Bearer accessToken";
        Long postIdx = 1L;

        given(jwtTokenService.getClaim(token, "id")).willReturn(memberId);
        given(jwtTokenService.getClaim(token, "role")).willReturn(Authority.MEMBER.getValue());

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/community/" + postIdx)
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }


}