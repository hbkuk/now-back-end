package com.now.config.document.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.now.core.attachment.application.AttachmentService;
import com.now.core.authentication.application.JwtTokenService;
import com.now.core.comment.application.CommentService;
import com.now.core.member.application.MemberService;
import com.now.core.member.presentation.MemberController;
import com.now.core.post.application.CommunityService;
import com.now.core.post.application.InquiryService;
import com.now.core.post.application.NoticeService;
import com.now.core.post.application.PhotoService;
import com.now.core.post.presentation.CommunityController;
import com.now.core.post.presentation.InquiryController;
import com.now.core.post.presentation.NoticeController;
import com.now.core.post.presentation.PhotoController;
import org.mybatis.spring.boot.test.autoconfigure.AutoConfigureMybatis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.operation.preprocess.OperationRequestPreprocessor;
import org.springframework.restdocs.operation.preprocess.OperationResponsePreprocessor;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;

@WebMvcTest({
        MemberController.class,
        NoticeController.class,
        CommunityController.class,
        PhotoController.class,
        InquiryController.class
})
@AutoConfigureMybatis
public abstract class ControllerTest {

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected MockMvc mockMvc;

    @MockBean
    protected MemberService memberService;

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


    protected OperationResponsePreprocessor getResponsePreprocessor() {
        return Preprocessors.preprocessResponse(prettyPrint());
    }

    protected OperationRequestPreprocessor getRequestPreprocessor() {
        return Preprocessors.preprocessRequest(prettyPrint());
    }

    protected String createJson(Object dto) throws JsonProcessingException {
        return objectMapper.writeValueAsString(dto);
    }
}