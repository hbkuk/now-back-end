package com.now.config.document.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.now.common.config.infrastructure.RateLimitBucketMap;
import com.now.common.config.infrastructure.RateLimitingProvider;
import com.now.common.mapper.EnumMapperFactory;
import com.now.core.admin.post.notice.application.ManagerNoticeService;
import com.now.core.admin.post.notice.presentation.ManagerNoticeController;
import com.now.core.attachment.application.AttachmentService;
import com.now.core.attachment.application.StorageService;
import com.now.core.attachment.presentation.AttachmentController;
import com.now.core.authentication.application.AuthenticationIntegratedService;
import com.now.core.authentication.application.AuthenticationService;
import com.now.core.authentication.application.JwtTokenProvider;
import com.now.core.authentication.application.TokenBlackList;
import com.now.core.authentication.presentation.AuthenticationContext;
import com.now.core.authentication.presentation.AuthenticationController;
import com.now.core.authentication.presentation.client.ClientContext;
import com.now.core.category.presentation.CategoryController;
import com.now.core.comment.application.CommentService;
import com.now.core.comment.presentation.CommentController;
import com.now.core.member.application.MemberService;
import com.now.core.member.presentation.MemberController;
import com.now.core.post.common.application.PostService;
import com.now.core.post.common.presentation.PostController;
import com.now.core.post.community.application.CommunityIntegratedService;
import com.now.core.post.community.application.CommunityService;
import com.now.core.post.community.presentation.CommunityController;
import com.now.core.post.inquiry.application.InquiryIntegratedService;
import com.now.core.post.inquiry.application.InquiryService;
import com.now.core.post.inquiry.presentation.InquiryController;
import com.now.core.post.notice.application.NoticeIntegratedService;
import com.now.core.post.notice.application.NoticeService;
import com.now.core.post.notice.presentation.NoticeController;
import com.now.core.post.photo.application.PhotoIntegratedService;
import com.now.core.post.photo.application.PhotoService;
import com.now.core.post.photo.presentation.PhotoController;
import com.now.core.report.application.ReportService;
import com.now.core.report.presentation.ReportController;
import org.mybatis.spring.boot.test.autoconfigure.AutoConfigureMybatis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest({
        ManagerNoticeController.class,
        AuthenticationController.class,
        MemberController.class,
        PostController.class,
        NoticeController.class,
        CommunityController.class,
        PhotoController.class,
        InquiryController.class,
        CategoryController.class,
        CommentController.class,
        AttachmentController.class,
        ReportController.class,
})
@AutoConfigureMybatis
@ActiveProfiles("test")
public abstract class ControllerTest {

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected MockMvc mockMvc;

    @MockBean
    protected NoticeIntegratedService noticeIntegratedService;

    @MockBean
    protected CommunityIntegratedService communityIntegratedService;

    @MockBean
    protected PhotoIntegratedService photoIntegratedService;

    @MockBean
    protected InquiryIntegratedService inquiryIntegratedService;

    @MockBean
    protected AuthenticationIntegratedService authenticationIntegratedService;

    @MockBean
    protected PostService postService;

    @MockBean
    protected ManagerNoticeService managerNoticeService;

    @MockBean
    protected MemberService memberService;

    @MockBean
    protected EnumMapperFactory enumMapperFactory;

    @MockBean
    protected NoticeService noticeService;

    @MockBean
    protected CommunityService communityService;

    @MockBean
    protected PhotoService photoService;

    @MockBean
    protected InquiryService inquiryService;

    @MockBean
    protected StorageService storageService;

    @MockBean
    protected AttachmentService attachmentService;

    @MockBean
    protected CommentService commentService;

    @MockBean
    protected ReportService reportService;

    @MockBean
    protected JwtTokenProvider jwtTokenProvider;

    @MockBean
    protected RateLimitingProvider rateLimitingProvider;

    @MockBean
    protected RateLimitBucketMap rateLimitBucketMap;

    @MockBean
    protected TokenBlackList tokenBlackList;

    @MockBean
    protected AuthenticationService authenticationService;

    @MockBean
    protected AuthenticationContext authenticationContext;

    @MockBean
    protected ClientContext senderContext;
}