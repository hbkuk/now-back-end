package com.now.core.post.presentation;

import com.now.config.document.utils.RestDocsTestSupport;
import com.now.config.fixtures.post.PhotoFixture;
import com.now.core.authentication.application.JwtTokenService;
import com.now.core.authentication.constants.Authority;
import com.now.core.category.domain.constants.Category;
import com.now.core.post.photo.domain.Photo;
import com.now.core.post.common.domain.constants.UpdateOption;
import com.now.core.post.common.presentation.dto.Condition;
import com.now.core.post.photo.presentation.dto.PhotosResponse;
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

import static com.now.config.document.snippet.RequestCookiesSnippet.cookieWithName;
import static com.now.config.document.snippet.RequestCookiesSnippet.customRequestHeaderCookies;
import static com.now.config.document.utils.RestDocsConfig.field;
import static com.now.config.fixtures.attachment.AttachmentFixture.createAttachments;
import static com.now.config.fixtures.comment.CommentFixture.createComments;
import static com.now.config.fixtures.post.CommunityFixture.SAMPLE_NICKNAME_1;
import static com.now.config.fixtures.post.CommunityFixture.SAMPLE_NICKNAME_2;
import static com.now.config.fixtures.post.PhotoFixture.createPhoto;
import static com.now.config.fixtures.post.PhotoFixture.createPhotoForSave;
import static com.now.config.fixtures.post.dto.ConditionFixture.createCondition;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("사진 컨트롤러는")
class PhotoControllerTest extends RestDocsTestSupport {

    @Test
    @DisplayName("모든 사진 게시글 조회")
    void getAllPhotos() throws Exception {
        // given
        Condition condition = createCondition(Category.DAILY_LIFE).updatePage();

        PhotosResponse photosResponse = PhotosResponse.builder()
                .photos(List.of(
                        createPhoto(1L, SAMPLE_NICKNAME_1, PhotoFixture.SAMPLE_TITLE_1, PhotoFixture.SAMPLE_CONTENT_1, createAttachments(), createComments()),
                        createPhoto(2L, SAMPLE_NICKNAME_2, PhotoFixture.SAMPLE_TITLE_2, PhotoFixture.SAMPLE_CONTENT_2, createAttachments(), createComments())
                ))
                .page(createCondition(Category.COMMUNITY_STUDY).updatePage().getPage().calculatePageInfo(2L))
                .build();

        given(photoIntegratedService.getAllPhotosWithPageInfo(condition)).willReturn(photosResponse);

        // when, then
        ResultActions resultActions =
                mockMvc.perform(RestDocumentationRequestBuilders.get("/api/photos")
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
    @DisplayName("사진 게시글 응답")
    void getPhoto() throws Exception {
        // given
        Long postIdx = 1L;
        given(photoIntegratedService.getPhotoAndIncrementViewCount(postIdx))
                .willReturn(createPhoto(
                        1L, PhotoFixture.SAMPLE_NICKNAME_1, PhotoFixture.SAMPLE_TITLE_1, PhotoFixture.SAMPLE_CONTENT_1, createAttachments(), createComments()));

        // when, then
        ResultActions resultActions = mockMvc.perform(RestDocumentationRequestBuilders.get("/api/photos/{postIdx}", postIdx)
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
                                fieldWithPath("content").type(STRING).description("내용"),
                                fieldWithPath("viewCount").type(NUMBER).description("조회수"),
                                fieldWithPath("likeCount").type(NUMBER).description("좋아요 수"),
                                fieldWithPath("dislikeCount").type(NUMBER).description("싫어요 수"),
                                fieldWithPath("category").type(STRING).description("카테고리"),
                                fieldWithPath("thumbnailAttachmentIdx").type(NUMBER).description("대표 이미지로 설정된 첨부파일 ID"),

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
    @DisplayName("사진 게시글 등록")
    void registerPhoto() throws Exception {
        String memberId = "tester1";
        String accessToken = "Bearer accessToken";
        Photo photo = createPhotoForSave().updatePostIdx(1L);
        given(jwtTokenService.getClaim(accessToken, "id")).willReturn(memberId);
        given(jwtTokenService.getClaim(accessToken, "role")).willReturn(Authority.MEMBER.getValue());

        MockMultipartFile communityPart = new MockMultipartFile("photo", "",
                MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsBytes(photo));

        MockMultipartFile fileA = new MockMultipartFile("attachments", "file1.png",
                MediaType.MULTIPART_FORM_DATA_VALUE, "file1 content".getBytes());

        MockMultipartFile fileB = new MockMultipartFile("attachments", "file2.png",
                MediaType.MULTIPART_FORM_DATA_VALUE, "file2 content".getBytes());

        MockMultipartFile fileC = new MockMultipartFile("thumbnail", "file3.png",
                MediaType.MULTIPART_FORM_DATA_VALUE, "file3 content".getBytes());

        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.multipart("/api/photos")
                        .file(communityPart)
                        .file(fileA)
                        .file(fileB)
                        .file(fileC)
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
                                partWithName("photo").description("사진 게시글 정보"),
                                partWithName("attachments").description("첨부파일 (다중 파일 업로드 가능)").optional(),
                                partWithName("thumbnail").description("대표이미지 첨부파일").optional()

                        ),
                        requestPartFields("photo",
                                fieldWithPath("postGroup").ignored(),
                                fieldWithPath("postIdx").ignored(),
                                fieldWithPath("category").description("카테고리"),
                                fieldWithPath("title").description("제목").attributes(field("constraints", "길이 100 이하")),
                                fieldWithPath("content").description("내용").attributes(field("constraints", "길이 2000 이하")),
                                fieldWithPath("thumbnailAttachmentIdx").description("대표 이미지로 설정된 첨부파일 ID")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("생성된 위치 URI")
                        )
                ));
    }

    @Test
    @DisplayName("사진 게시글 수정(기존 이미지 수정)")
    void updatePhoto_EditExisting() throws Exception {
        Long postIdx = 1L;
        String memberId = "tester1";
        String accessToken = "Bearer accessToken";
        given(jwtTokenService.getClaim(accessToken, "id")).willReturn(memberId);
        given(jwtTokenService.getClaim(accessToken, "role")).willReturn(Authority.MEMBER.getValue());

        Photo updatedPhoto = Photo.builder()
                .category(Category.ARTWORK)
                .title("수정된 제목")
                .content("수정된 내용")
                .thumbnailAttachmentIdx(1L)
                .build();

        MockMultipartFile updateOptionPart = new MockMultipartFile("updateOption", "",
                MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsBytes(UpdateOption.EDIT_EXISTING));

        MockMultipartFile communityPart = new MockMultipartFile("photo", "",
                MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsBytes(updatedPhoto));

        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.multipart("/api/photos/{postIdx}", postIdx)
                        .file(updateOptionPart)
                        .file(communityPart)
                        .param("thumbnailAttachmentIdx", "1")
                        .param("notDeletedIndexes", "1", "2")
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
                                partWithName("updateOption").description("사진 게시글 수정 타입"),
                                partWithName("photo").description("사진 게시글 정보")
                        ),
                        requestPartFields("photo",
                                fieldWithPath("postGroup").ignored(),
                                fieldWithPath("category").description("카테고리"),
                                fieldWithPath("title").description("제목").attributes(field("constraints", "길이 100 이하")),
                                fieldWithPath("content").description("내용").attributes(field("constraints", "길이 2000 이하")),
                                fieldWithPath("thumbnailAttachmentIdx").ignored().description("대표 이미지로 설정된 첨부파일 ID")
                        ),
                        requestParameters(
                                parameterWithName("thumbnailAttachmentIdx").description("수정할 대표 이미지로 지정할 첨부 파일 번호").optional(),
                                parameterWithName("notDeletedIndexes").description("삭제하지 않을 첨부 파일 번호 목록").optional()
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("생성된 위치 URI")
                        )
                ));
    }

    @Test
    @DisplayName("사진 게시글 수정")
    void updatePhotoAddNew() throws Exception {
        Long postIdx = 1L;
        String memberId = "tester1";
        String accessToken = "Bearer accessToken";
        given(jwtTokenService.getClaim(accessToken, "id")).willReturn(memberId);
        given(jwtTokenService.getClaim(accessToken, "role")).willReturn(Authority.MEMBER.getValue());

        Photo updatedPhoto = Photo.builder()
                .category(Category.ARTWORK)
                .title("수정된 제목")
                .content("수정된 내용")
                .thumbnailAttachmentIdx(1L)
                .build();

        MockMultipartFile updateOptionPart = new MockMultipartFile("updateOption", "",
                MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsBytes(UpdateOption.ADD_NEW));

        MockMultipartFile communityPart = new MockMultipartFile("photo", "",
                MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsBytes(updatedPhoto));

        MockMultipartFile fileA = new MockMultipartFile("attachment", "file1.png",
                MediaType.MULTIPART_FORM_DATA_VALUE, "file1 content".getBytes());

        MockMultipartFile fileB = new MockMultipartFile("attachment", "file2.png",
                MediaType.MULTIPART_FORM_DATA_VALUE, "file2 content".getBytes());

        MockMultipartFile fileC = new MockMultipartFile("thumbnail", "file3.png",
                MediaType.MULTIPART_FORM_DATA_VALUE, "file3 content".getBytes());

        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.multipart("/api/photos/{postIdx}", postIdx)
                        .file(updateOptionPart)
                        .file(communityPart)
                        .file(fileA)
                        .file(fileB)
                        .file(fileC)
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
                                partWithName("updateOption").description("사진 게시글 수정 타입"),
                                partWithName("photo").description("사진 게시글 정보"),
                                partWithName("thumbnail").description("대표이미지 첨부파일").optional(),
                                partWithName("attachment").description("첨부파일 (다중 파일 업로드 가능)").optional()
                        ),
                        requestPartFields("photo",
                                fieldWithPath("postGroup").ignored(),
                                fieldWithPath("category").description("카테고리"),
                                fieldWithPath("title").description("제목").attributes(field("constraints", "길이 100 이하")),
                                fieldWithPath("content").description("내용").attributes(field("constraints", "길이 2000 이하")),
                                fieldWithPath("thumbnailAttachmentIdx").ignored().description("대표 이미지로 설정된 첨부파일 ID")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("생성된 위치 URI")
                        )
                ));
    }

    @Test
    @DisplayName("사진 게시글 삭제")
    void deletePhoto() throws Exception {
        Long postIdx = 1L;
        String memberId = "tester1";
        String accessToken = "Bearer accessToken";
        given(jwtTokenService.getClaim(accessToken, "id")).willReturn(memberId);
        given(jwtTokenService.getClaim(accessToken, "role")).willReturn(Authority.MEMBER.getValue());

        ResultActions resultActions = mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/photos/{postIdx}", postIdx)
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