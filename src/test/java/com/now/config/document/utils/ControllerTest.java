package com.now.config.document.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.now.common.mapper.EnumMapperFactory;
import com.now.core.admin.post.notice.application.ManagerNoticeService;
import com.now.core.admin.post.notice.presentation.ManagerNoticeController;
import com.now.core.attachment.application.AttachmentService;
import com.now.core.attachment.presentation.AttachmentController;
import com.now.core.authentication.application.AuthenticationService;
import com.now.core.authentication.application.JwtTokenService;
import com.now.core.authentication.application.TokenBlackList;
import com.now.core.authentication.presentation.AuthenticationContext;
import com.now.core.authentication.presentation.AuthenticationController;
import com.now.core.category.presentation.CategoryController;
import com.now.core.comment.application.CommentService;
import com.now.core.comment.presentation.CommentController;
import com.now.core.member.application.MemberService;
import com.now.core.member.presentation.MemberController;
import com.now.core.post.application.*;
import com.now.core.post.application.integrated.CommunityIntegratedService;
import com.now.core.post.application.integrated.NoticeIntegratedService;
import com.now.core.post.presentation.*;
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
        AttachmentController.class
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
    protected AttachmentService attachmentService;

    @MockBean
    protected CommentService commentService;

    @MockBean
    protected JwtTokenService jwtTokenService;

    @MockBean
    protected TokenBlackList tokenBlackList;

    @MockBean
    protected AuthenticationService authenticationService;

    @MockBean
    protected AuthenticationContext authenticationContext;
}