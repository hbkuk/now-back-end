package com.now.core.post.application;

import com.now.NowApplication;
import com.now.common.exception.ErrorType;
import com.now.core.comment.application.CommentService;
import com.now.core.member.application.MemberService;
import com.now.core.post.domain.PostRepository;
import com.now.core.post.exception.InvalidPostException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = NowApplication.class)
public class CommunityServiceTest {

    @Autowired private CommunityService communityService;
    @MockBean private PostRepository postRepository;
    @MockBean private MemberService memberService;
    @MockBean private CommentService commentService;

    @Test
    @DisplayName("커뮤니티 게시글을 찾을때 반환값이 null이라면 InvalidPostException을 던진다")
    void findByPostIdx() {
        Long postIdx = 1L;
        when(postRepository.findCommunity(postIdx)).thenReturn(null);

        assertThatExceptionOfType(InvalidPostException.class)
                .isThrownBy(() -> {
                    communityService.getCommunity(postIdx);
                })
                .withMessage(ErrorType.NOT_FOUND_POST.getMessage());
    }
}
