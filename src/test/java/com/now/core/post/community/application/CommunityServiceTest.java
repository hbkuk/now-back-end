package com.now.core.post.community.application;

import com.now.common.exception.ErrorType;
import com.now.config.annotations.ApplicationTest;
import com.now.config.fixtures.comment.CommentFixture;
import com.now.core.category.domain.constants.Category;
import com.now.core.category.exception.InvalidCategoryException;
import com.now.core.comment.domain.Comment;
import com.now.core.comment.domain.CommentRepository;
import com.now.core.member.domain.Member;
import com.now.core.member.domain.MemberRepository;
import com.now.core.member.exception.InvalidMemberException;
import com.now.core.post.community.domain.Community;
import com.now.core.post.community.domain.repository.CommunityRepository;
import com.now.core.post.common.exception.CannotDeletePostException;
import com.now.core.post.common.exception.CannotUpdatePostException;
import com.now.core.post.common.exception.InvalidPostException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

import static com.now.config.fixtures.member.MemberFixture.createMember;
import static com.now.config.fixtures.post.CommunityFixture.createCommunity;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ApplicationTest
@DisplayName("커뮤니티 서비스 객체는")
class CommunityServiceTest {

    @Autowired private CommunityService communityService;
    @MockBean private CommunityRepository communityRepository;
    @MockBean private MemberRepository memberRepository;
    @MockBean private CommentRepository commentRepository;

    @Test
    @DisplayName("커뮤니티 게시글을 찾을때 게시물 번호에 해당하는 게시물이 없다면 InvalidPostException 을 던진다")
    void getCommunity() {
        Long postIdx = 1L;
        when(communityRepository.findCommunity(postIdx)).thenReturn(null);

        assertThatExceptionOfType(InvalidPostException.class)
                .isThrownBy(() -> {
                    communityService.getCommunity(postIdx);
                })
                .withMessage(ErrorType.NOT_FOUND_POST.getMessage());
    }

    @Nested
    @DisplayName("게시글 수정 권한 확인 메서드는")
    class HasUpdateAccess_of {

        @Test
        @DisplayName("본인이 작성한 게시글이면 true 를 반환한다")
        void hasUpdateAccess() {
            Long postIdx = 1L;
            Member member = createMember("tester1");
            Community community = createCommunity("tester1", Category.COMMUNITY_STUDY);

            when(communityRepository.findCommunity(postIdx)).thenReturn(community);
            when(memberRepository.findById("tester1")).thenReturn(member);

            communityService.hasUpdateAccess(postIdx, "tester1");
        }

        @Test
        @DisplayName("다른 회원이 작성한 게시글이라면 CannotUpdatePostException 을 던진다")
        void hasUpdateAccess_throw_CannotUpdatePostException() {
            Long postIdx = 1L;
            Member member = createMember("tester2");
            Community community = createCommunity("tester1", Category.COMMUNITY_STUDY);

            when(communityRepository.findCommunity(anyLong())).thenReturn(community);
            when(memberRepository.findById(anyString())).thenReturn(member);

            assertThatExceptionOfType(CannotUpdatePostException.class)
                    .isThrownBy(() -> {
                        communityService.hasUpdateAccess(postIdx, "tester2");
                    })
                    .withMessage(ErrorType.CAN_NOT_UPDATE_OTHER_MEMBER_POST.getMessage());
        }
    }

    @Nested
    @DisplayName("게시글 삭제 권한 확인 메서드는")
    class HasDeleteAccess_of {

        @Test
        @DisplayName("본인이 작성한 게시글이면 true 를 반환한다")
        void hasDeleteAccess() {
            Long postIdx = 1L;
            Member member = createMember("tester1");
            Community community = createCommunity("tester1", Category.COMMUNITY_STUDY);

            when(communityRepository.findCommunity(postIdx)).thenReturn(community);
            when(memberRepository.findById("tester1")).thenReturn(member);

            communityService.hasDeleteAccess(postIdx, "tester1");
        }

        @Test
        @DisplayName("다른 회원이 작성한 게시글이라면 CannotDeletePostException 을 던진다")
        void hasDeleteAccess_throw_CannotDeletePostException() {
            Long postIdx = 1L;
            Member member = createMember("tester2");
            Community community = createCommunity("tester1", Category.COMMUNITY_STUDY);

            when(communityRepository.findCommunity(anyLong())).thenReturn(community);
            when(memberRepository.findById(anyString())).thenReturn(member);

            assertThatExceptionOfType(CannotDeletePostException.class)
                    .isThrownBy(() -> {
                        communityService.hasDeleteAccess(postIdx, "tester2");
                    })
                    .withMessage(ErrorType.CAN_NOT_DELETE_OTHER_MEMBER_POST.getMessage());
        }

        @Test
        @DisplayName("다른 회원이 작성한 댓글이 있다면 CannotDeletePostException 을 던진다")
        void hasDeleteAccess_throw_CannotDeletePostException_2() {
            Long postIdx = 1L;
            Member member = createMember("tester1");
            Community community = createCommunity("tester1", Category.COMMUNITY_STUDY);
            List<Comment> comments = List.of(CommentFixture.createCommentByMemberId("tester3"));

            when(communityRepository.findCommunity(anyLong())).thenReturn(community);
            when(memberRepository.findById(anyString())).thenReturn(member);
            when(commentRepository.findAllByPostIdx(anyLong())).thenReturn(comments);

            assertThatExceptionOfType(CannotDeletePostException.class)
                    .isThrownBy(() -> {
                        communityService.hasDeleteAccess(postIdx, "tester1");
                    })
                    .withMessage(ErrorType.CAN_NOT_DELETE_POST_WITH_OTHER_MEMBER_COMMENTS.getMessage());
        }
    }

    @DisplayName("게시글 등록 메서드는")
    @Nested
    class RegisterCommunity_of {

        @Test
        @DisplayName("회원 정보가 있어야하고, 허용되는 카테고리여야 한다")
        void registerCommunity() {
            Long postIdx = 1L;
            Member member = createMember("tester1");
            Community community = createCommunity("tester1", Category.COMMUNITY_STUDY);

            when(memberRepository.findById("tester1")).thenReturn(member);

            communityService.registerCommunity(community);
        }

        @Test
        @DisplayName("회원 정보가 없다면 InvalidMemberException 을 던진다")
        void registerCommunity_throw_eInvalidMemberException() {
            Long postIdx = 1L;
            Community community = createCommunity("tester1", Category.COMMUNITY_STUDY);

            when(memberRepository.findById("tester1")).thenReturn(null);

            assertThatExceptionOfType(InvalidMemberException.class)
                    .isThrownBy(() -> {
                        communityService.registerCommunity(community);
                    })
                    .withMessage(ErrorType.NOT_FOUND_MEMBER.getMessage());
        }

        @Test
        @DisplayName("허용되지 않는 카테고리일 경우 InvalidCategoryException 을 던진다")
        void registerCommunity_throw_InvalidCategoryException() {
            Long postIdx = 1L;
            Member member = createMember("tester1");
            Community community = createCommunity("tester1", Category.NEWS);

            when(memberRepository.findById("tester1")).thenReturn(member);

            assertThatExceptionOfType(InvalidCategoryException.class)
                    .isThrownBy(() -> {
                        communityService.registerCommunity(community);
                    })
                    .withMessage(ErrorType.INVALID_CATEGORY.getMessage());
        }
    }

    @DisplayName("게시글 수정 메서드는")
    @Nested
    class UpdateCommunity_of {

        @Test
        @DisplayName("회원 정보가 있어야하고, 허용되는 카테고리여야 한다")
        void registerCommunity() {
            Long postIdx = 1L;
            Member member = createMember("tester1");
            Community community = createCommunity("tester1", Category.COMMUNITY_STUDY);

            when(memberRepository.findById("tester1")).thenReturn(member);

            communityService.updateCommunity(community);
        }

        @Test
        @DisplayName("회원 정보가 없다면 InvalidMemberException 을 던진다")
        void registerCommunity_throw_eInvalidMemberException() {
            Long postIdx = 1L;
            Community community = createCommunity("tester1", Category.COMMUNITY_STUDY);

            when(memberRepository.findById("tester1")).thenReturn(null);

            assertThatExceptionOfType(InvalidMemberException.class)
                    .isThrownBy(() -> {
                        communityService.updateCommunity(community);
                    })
                    .withMessage(ErrorType.NOT_FOUND_MEMBER.getMessage());
        }

        @Test
        @DisplayName("허용되지 않는 카테고리일 경우 InvalidCategoryException 을 던진다")
        void registerCommunity_throw_InvalidCategoryException() {
            Long postIdx = 1L;
            Member member = createMember("tester1");
            Community community = createCommunity("tester1", Category.NEWS);

            when(memberRepository.findById("tester1")).thenReturn(member);

            assertThatExceptionOfType(InvalidCategoryException.class)
                    .isThrownBy(() -> {
                        communityService.updateCommunity(community);
                    })
                    .withMessage(ErrorType.INVALID_CATEGORY.getMessage());
        }
    }
}
