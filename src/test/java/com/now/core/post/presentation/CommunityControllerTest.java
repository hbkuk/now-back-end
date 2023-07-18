package com.now.core.post.presentation;

import com.now.core.comment.application.CommentService;
import com.now.core.member.application.MemberService;
import com.now.core.post.application.CommunityService;
import com.now.core.post.domain.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

public class CommunityControllerTest {

    @Autowired private CommunityService communityService;
    @MockBean private PostRepository postRepository;
    @MockBean private MemberService memberService;
    @MockBean private CommentService commentService;


}
