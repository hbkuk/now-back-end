package com.now.core.post.inquiry.application;

import com.now.common.exception.ErrorType;
import com.now.common.security.PasswordSecurityManager;
import com.now.config.annotations.ApplicationTest;
import com.now.config.fixtures.comment.CommentFixture;
import com.now.config.fixtures.post.InquiryFixture;
import com.now.core.category.domain.constants.Category;
import com.now.core.comment.domain.Comment;
import com.now.core.comment.domain.CommentRepository;
import com.now.core.member.domain.Member;
import com.now.core.member.domain.MemberRepository;
import com.now.core.post.inquiry.domain.Inquiry;
import com.now.core.post.inquiry.domain.repository.InquiryRepository;
import com.now.core.post.common.exception.CannotDeletePostException;
import com.now.core.post.common.exception.CannotUpdatePostException;
import com.now.core.post.inquiry.exception.CannotViewInquiryException;
import com.now.core.post.common.exception.InvalidPostException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

import static com.now.config.fixtures.member.MemberFixture.createMember;
import static com.now.config.fixtures.post.InquiryFixture.createSecretInquiryWithAnswer;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ApplicationTest
@DisplayName("문의 서비스 객체는")
class InquiryServiceTest {

    @Autowired private InquiryService inquiryService;
    @MockBean private InquiryRepository inquiryRepository;
    @MockBean private MemberRepository memberRepository;
    @MockBean private CommentRepository commentRepository;
    @MockBean private PasswordSecurityManager passwordSecurityManager;

    @Test
    @DisplayName("문의 게시글을 찾을때 게시물 번호에 해당하는 게시물이 없다면 InvalidPostException 을 던진다")
    void getCommunity() {
        Long postIdx = 1L;
        when(inquiryRepository.findInquiry(postIdx)).thenReturn(null);

        assertThatExceptionOfType(InvalidPostException.class)
                .isThrownBy(() -> {
                    inquiryService.getInquiry(postIdx);
                })
                .withMessage(ErrorType.NOT_FOUND_POST.getMessage());
    }

    @Nested
    @DisplayName("비밀글 설정 체크를 포함한 문의 게시글을 찾을 때")
    class GetInquiryWithSecretCheck_of {

        @Test
        @DisplayName("비밀글 설정이 아닐 때, 문의 게시글을 반환한다")
        void getInquiryWithSecretCheck() {
            Long postIdx = 1L;
            Inquiry inquiry = InquiryFixture.createNonSecretInquiry("tester1", Category.SERVICE);

            when(inquiryRepository.findInquiry(postIdx)).thenReturn(inquiry);

            inquiryService.getPrivateInquiry(postIdx, "tester1", "token");
        }

        @Nested
        @DisplayName("비밀글 설정일 때")
        class Secret {

            @Test
            @DisplayName("비밀번호를 확인 후 같다면 문의 게시글을 반환한다.")
            void getInquiryWithSecretCheck_isPasswordMatching() {
                Long postIdx = 1L;
                Inquiry inquiry = InquiryFixture.createSecretInquiry("tester1", Category.SERVICE);

                when(inquiryRepository.findInquiry(postIdx)).thenReturn(inquiry);
                when(passwordSecurityManager.matchesWithSalt(anyString(), anyString())).thenReturn(true);

                inquiryService.getPrivateInquiry(postIdx, null, "testPassword");
            }

            @Test
            @DisplayName("본인의 게시글이라면 문의 게시글을 반환한다.")
            void getInquiryWithSecretCheck_SameMemberId() {
                Long postIdx = 1L;
                Member member = createMember("tester1");
                Inquiry inquiry = InquiryFixture.createSecretInquiry("tester1", Category.SERVICE);

                when(inquiryRepository.findInquiry(postIdx)).thenReturn(inquiry);
                when(memberRepository.findById(anyString())).thenReturn(member);

                inquiryService.getPrivateInquiry(postIdx, "tester1", null);
            }

            @Test
            @DisplayName("본인의 게시글이 아니라면 CannotViewInquiryException 을 던진다")
            void getInquiryWithSecretCheck_throw_CannotViewInquiryException() {
                Long postIdx = 1L;
                Member member = createMember("tester2");
                Inquiry inquiry = InquiryFixture.createSecretInquiry("tester1", Category.SERVICE);

                when(inquiryRepository.findInquiry(postIdx)).thenReturn(inquiry);
                when(memberRepository.findById(anyString())).thenReturn(member);

                assertThatExceptionOfType(CannotViewInquiryException.class)
                        .isThrownBy(() -> {
                            inquiryService.getPrivateInquiry(postIdx, "tester2", null);
                        })
                        .withMessage(ErrorType.CAN_NOT_VIEW_OTHER_MEMBER_INQUIRIES.getMessage());
            }
        }
    }

    @Nested
    @DisplayName("게시글 수정 권한 확인 메서드는")
    class HasUpdateAccess_of {

        @Test
        @DisplayName("본인이 작성한 게시글이면 true 를 반환한다")
        void hasUpdateAccess() {
            Long postIdx = 1L;
            Member member = createMember("tester1");
            Inquiry inquiry = InquiryFixture.createSecretInquiry("tester1", Category.SERVICE);

            when(inquiryRepository.findInquiry(postIdx)).thenReturn(inquiry);
            when(memberRepository.findById("tester1")).thenReturn(member);

            inquiryService.hasUpdateAccess(postIdx, "tester1");
        }

        @Test
        @DisplayName("다른 회원이 작성한 게시글이라면 CannotUpdatePostException 을 던진다")
        void hasUpdateAccess_throw_CannotUpdatePostException() {
            Long postIdx = 1L;
            Member member = createMember("tester2");
            Inquiry inquiry = InquiryFixture.createSecretInquiry("tester1", Category.SERVICE);

            when(inquiryRepository.findInquiry(postIdx)).thenReturn(inquiry);
            when(memberRepository.findById(anyString())).thenReturn(member);

            assertThatExceptionOfType(CannotUpdatePostException.class)
                    .isThrownBy(() -> {
                        inquiryService.hasUpdateAccess(postIdx, "tester2");
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
            Inquiry inquiry = InquiryFixture.createSecretInquiry("tester1", Category.SERVICE);

            when(inquiryRepository.findInquiry(postIdx)).thenReturn(inquiry);
            when(memberRepository.findById("tester1")).thenReturn(member);

            inquiryService.hasDeleteAccess(postIdx, "tester1");
        }

        @Test
        @DisplayName("다른 회원이 작성한 게시글이라면 CannotDeletePostException 을 던진다")
        void hasDeleteAccess_throw_CannotDeletePostException() {
            Long postIdx = 1L;
            Member member = createMember("tester2");
            Inquiry inquiry = InquiryFixture.createSecretInquiry("tester1", Category.SERVICE);

            when(inquiryRepository.findInquiry(postIdx)).thenReturn(inquiry);
            when(memberRepository.findById(anyString())).thenReturn(member);

            assertThatExceptionOfType(CannotDeletePostException.class)
                    .isThrownBy(() -> {
                        inquiryService.hasDeleteAccess(postIdx, "tester2");
                    })
                    .withMessage(ErrorType.CAN_NOT_DELETE_OTHER_MEMBER_POST.getMessage());
        }

        @Test
        @DisplayName("다른 회원이 작성한 댓글이 있다면 CannotDeletePostException 을 던진다")
        void hasDeleteAccess_throw_CannotDeletePostException_2() {
            Long postIdx = 1L;
            Member member = createMember("tester1");
            Inquiry inquiry = InquiryFixture.createSecretInquiry("tester1", Category.SERVICE);
            List<Comment> comments = List.of(CommentFixture.createCommentByMemberId("tester3"));

            when(inquiryRepository.findInquiry(postIdx)).thenReturn(inquiry);
            when(memberRepository.findById(anyString())).thenReturn(member);
            when(commentRepository.findAllByPostIdx(anyLong())).thenReturn(comments);

            assertThatExceptionOfType(CannotDeletePostException.class)
                    .isThrownBy(() -> {
                        inquiryService.hasDeleteAccess(postIdx, "tester1");
                    })
                    .withMessage(ErrorType.CAN_NOT_DELETE_POST_WITH_OTHER_MEMBER_COMMENTS.getMessage());
        }

        @Test
        @DisplayName("이미 매니저가 답변했다면 CannotDeletePostException 을 던진다")
        void hasDeleteAccess_throw_CannotDeletePostException_3() {
            Long postIdx = 1L;
            Member member = createMember("tester1");
            Inquiry inquiry = createSecretInquiryWithAnswer("tester1", Category.SERVICE);
            List<Comment> comments = List.of(CommentFixture.createCommentByMemberId("tester1"));

            when(inquiryRepository.findInquiry(postIdx)).thenReturn(inquiry);
            when(memberRepository.findById(anyString())).thenReturn(member);
            when(commentRepository.findAllByPostIdx(anyLong())).thenReturn(comments);

            assertThatExceptionOfType(CannotDeletePostException.class)
                    .isThrownBy(() -> {
                        inquiryService.hasDeleteAccess(postIdx, "tester1");
                    })
                    .withMessage(ErrorType.CAN_NOT_DELETE_POST_WITH_MANAGER_ANSWER.getMessage());
        }
    }
}
