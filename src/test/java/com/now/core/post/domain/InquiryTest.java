package com.now.core.post.domain;

import com.now.config.fixtures.comment.CommentFixture;
import com.now.config.fixtures.member.MemberFixture;
import com.now.config.fixtures.post.InquiryFixture;
import com.now.core.category.domain.constants.Category;
import com.now.core.comment.domain.Comment;
import com.now.core.member.domain.Member;
import com.now.core.post.common.exception.CannotDeletePostException;
import com.now.core.post.common.exception.CannotUpdatePostException;
import com.now.core.post.inquiry.domain.Inquiry;
import com.now.core.post.inquiry.exception.CannotViewInquiryException;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.now.config.fixtures.post.InquiryFixture.createNonSecretInquiry;
import static com.now.config.fixtures.post.InquiryFixture.createSecretInquiry;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@DisplayName("Inquiry 도메인 객체")
public class InquiryTest {

    @Nested
    @DisplayName("canUpdate 메서드는")
    class CanUpdate_of {

        @Test
        @DisplayName("Member 객체가 매개변수로 전달될 때, 동일한 회원이라면 true를 반환한다.")
        void return_true_when_same_user() {
            // given
            Member member = MemberFixture.createMember("tester1");
            Inquiry inquiry = InquiryFixture.createSecretInquiry("tester1");

            // when, then
            assertThat(inquiry.canUpdate(member)).isTrue();
        }

        @Test
        @DisplayName("Member 객체가 매개변수로 전달될 때, 다른 회원이라면 CannotUpdatePostException을 던진다.")
        void throw_exception_when_not_same_user() {
            // given
            Member member = MemberFixture.createMember("tester1");
            Inquiry inquiry = InquiryFixture.createSecretInquiry("tester2");

            // when, then
            AssertionsForClassTypes.assertThatExceptionOfType(CannotUpdatePostException.class)
                    .isThrownBy(() -> {
                        inquiry.canUpdate(member);
                    })
                    .withMessageMatching("다른 회원이 작성한 게시글을 수정할 수 없습니다.");
        }
    }

    @Nested
    @DisplayName("canDelete 메서드는")
    class CanDelete_of {

        @Nested
        @DisplayName("동일한 회원")
        class SameUser {

            @Test
            @DisplayName("댓글이 하나도 없을 경우 true를 반환한다.")
            void return_true_when_nothing_comment() {
                // given
                Member member = MemberFixture.createMember("tester1");
                Inquiry inquiry = InquiryFixture.createSecretInquiry("tester1");

                // when, then
                assertThat(inquiry.canDelete(member, new ArrayList<Comment>())).isTrue();

            }

            @Test
            @DisplayName("댓글 작성자도 같다면 true를 반환한다.")
            void return_true_when_same_comment_author() {
                // given
                Member member = MemberFixture.createMember("tester1");
                Inquiry inquiry = InquiryFixture.createSecretInquiry("tester1");
                List<Comment> comments = Arrays.asList(CommentFixture.createCommentByMemberId("tester1"));

                // when, then
                assertThat(inquiry.canDelete(member, comments)).isTrue();
            }

            @Test
            @DisplayName("댓글 작성자가 다르다면 CanDeletePostException을 던진다.")
            void throw_exception_when_not_same_comment_author() {
                // given
                Member member = MemberFixture.createMember("tester1");
                Inquiry inquiry = InquiryFixture.createSecretInquiry("tester1");
                List<Comment> comments = Arrays.asList(CommentFixture.createCommentByMemberId("tester2"));

                // when, then
                AssertionsForClassTypes.assertThatExceptionOfType(CannotDeletePostException.class)
                        .isThrownBy(() -> {
                            inquiry.canDelete(member, comments);
                        })
                        .withMessageMatching("다른 회원이 작성한 댓글이 있으므로 해당 게시글을 삭제할 수 없습니다.");
            }

            @Test
            @DisplayName("댓글 작성자도 같고, 답변이 있다면 CanDeletePostException을 던진다.")
            void throw_exception_when_not_existing_manager_answer() {
                // given
                Member member = MemberFixture.createMember("tester1");
                Inquiry inquiry = InquiryFixture.createSecretInquiry("tester1");
                List<Comment> comments = Arrays.asList(CommentFixture.createCommentByMemberId("tester1"));

                // when, then
                assertThat(inquiry.canDelete(member, comments)).isTrue();
            }


            @Test
            @DisplayName("댓글 작성자도 같고, 답변이 있다면 CanDeletePostException을 던진다.")
            void throw_exception_when_existing_manager_answer() {
                // given
                Member member = MemberFixture.createMember("tester1");
                Inquiry inquiry = InquiryFixture.createSecretInquiryWithAnswer("tester1", Category.SERVICE);
                List<Comment> comments = Arrays.asList(CommentFixture.createCommentByMemberId("tester1"));

                // when, then
                AssertionsForClassTypes.assertThatExceptionOfType(CannotDeletePostException.class)
                        .isThrownBy(() -> {
                            inquiry.canDelete(member, comments);
                        })
                        .withMessageMatching("매니저가 작성한 답변이 있으므로 해당 게시글을 삭제할 수 없습니다.");
            }
        }

        @Nested
        @DisplayName("Member 객체가 매개변수로 전달될 때, 만약 다른 회원이이면서")
        class NotSameUser {

            @Test
            @DisplayName("댓글이 하나도 없을 경우 CanDeletePostException을 던진다.")
            void throw_exception_when_nothing_comment() {
                // given
                Member member = MemberFixture.createMember("tester1");
                Inquiry inquiry = InquiryFixture.createSecretInquiry("tester2");

                // when, then
                AssertionsForClassTypes.assertThatExceptionOfType(CannotDeletePostException.class)
                        .isThrownBy(() -> {
                            inquiry.canDelete(member, new ArrayList<Comment>());
                        })
                        .withMessageMatching("다른 회원이 작성한 게시글을 삭제할 수 없습니다.");
            }

            @Test
            @DisplayName("댓글이 있을 경우 CanDeletePostException을 던진다.")
            void throw_exception_when_exist_comment() {
                // given
                Member member = MemberFixture.createMember("tester1");
                Inquiry inquiry = InquiryFixture.createSecretInquiry("tester2");
                List<Comment> comments = Arrays.asList(CommentFixture.createCommentByMemberId("tester2"));

                // when, then
                AssertionsForClassTypes.assertThatExceptionOfType(CannotDeletePostException.class)
                        .isThrownBy(() -> {
                            inquiry.canDelete(member, comments);
                        })
                        .withMessageMatching("다른 회원이 작성한 게시글을 삭제할 수 없습니다.");
            }

            @Test
            @DisplayName("댓글이 같은 회원일 경우 CanDeletePostException을 던진다.")
            void throw_exception_when_same_comment_author() {
                // given
                Member member = MemberFixture.createMember("tester1");
                Inquiry inquiry = InquiryFixture.createSecretInquiry("tester2");
                List<Comment> comments = Arrays.asList(CommentFixture.createCommentByMemberId("tester1"));

                // when, then
                AssertionsForClassTypes.assertThatExceptionOfType(CannotDeletePostException.class)
                        .isThrownBy(() -> {
                            inquiry.canDelete(member, comments);
                        })
                        .withMessageMatching("다른 회원이 작성한 게시글을 삭제할 수 없습니다.");
            }
        }
    }

    @Nested
    @DisplayName("canView 메서드는")
    class CanView_of {

        @Nested
        @DisplayName("만약 비밀글 설정일때")
        class Secret_true {

            @Test
            @DisplayName("동일한 사용자라면 true를 반환한다.")
            void return_true_when_same_member() {
                // given
                Inquiry inquiry = createSecretInquiry("tester1");
                Member member = MemberFixture.createMember("tester1");

                // when, then
                assertThat(inquiry.canView(member)).isTrue();
            }

            @Test
            @DisplayName("다른 사용자라면 CannotViewInquiryException을 던진다.")
            void throw_exception_when_not_same_member() {
                // given
                Inquiry inquiry = createSecretInquiry("tester1");
                Member member = MemberFixture.createMember("tester2");

                // when, then
                assertThatExceptionOfType(CannotViewInquiryException.class)
                        .isThrownBy(() -> {
                            inquiry.canView(member);
                        })
                        .withMessage("다른 사용자가 작성한 문의글을 볼 수 없습니다.");
            }
        }

        @Nested
        @DisplayName("만약 비밀글 설정이 아닐때")
        class secret_false {

            @Test
            @DisplayName("동일한 사용자라면 true를 반환한다.")
            void return_true_when_same_member() {
                // given
                Inquiry inquiry = createNonSecretInquiry("tester1");
                Member member = MemberFixture.createMember("tester1");

                // when, then
                assertThat(inquiry.canView(member)).isTrue();
            }

            @Test
            @DisplayName("다른 사용자라면 ture를 반환한다.")
            void throw_exception_when_not_same_member() {
                // given
                Inquiry inquiry = createNonSecretInquiry("tester1");
                Member member = MemberFixture.createMember("tester2");

                // when, then
                assertThat(inquiry.canView(member)).isTrue();
            }
        }
    }
}
