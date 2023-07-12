package com.now.domain;

import com.now.domain.manager.Manager;
import com.now.domain.post.Inquiry;
import com.now.domain.user.User;
import com.now.exception.CannotViewInquiryException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static com.now.domain.ManagerTest.createManagerByManagerId;
import static com.now.domain.UserTest.createUserByUserId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class InquiryTest {
    public static Inquiry createSecretInquiryByAuthorId(String authorId) {
        return Inquiry.builder()
                .postIdx(1L)
                .title("제목")
                .authorId(authorId)
                .regDate(LocalDateTime.now())
                .modDate(LocalDateTime.now())
                .content("내용")
                .viewCount(0)
                .likeCount(0)
                .dislikeCount(0)
                .isSecret(true)
                .isAnswerCompleted(false)
                .build();
    }

    public static Inquiry createNonSecretInquiryByAuthorId(String authorId) {
        return Inquiry.builder()
                .postIdx(1L)
                .title("제목")
                .authorId(authorId)
                .regDate(LocalDateTime.now())
                .modDate(LocalDateTime.now())
                .content("내용")
                .viewCount(0)
                .likeCount(0)
                .dislikeCount(0)
                .isSecret(false)
                .isAnswerCompleted(false)
                .build();
    }

    @Nested
    @DisplayName("canView 메서드는")
    class CanView_of {

        @Nested
        @DisplayName("만약 비밀글 설정일때")
        class Secret_true {

            @Test
            @DisplayName("동일한 사용자라면 true를 반환한다.")
            void return_true_when_same_user() {
                // given
                Inquiry inquiry = createSecretInquiryByAuthorId("tester1");
                User user = createUserByUserId("tester1");

                // when, then
                assertThat(inquiry.canView(user)).isTrue();
            }

            @Test
            @DisplayName("다른 사용자라면 CannotViewInquiryException을 던진다.")
            void throw_exception_when_not_same_user() {
                // given
                Inquiry inquiry = createSecretInquiryByAuthorId("tester1");
                User user = createUserByUserId("tester2");

                // when, then
                assertThatExceptionOfType(CannotViewInquiryException.class)
                        .isThrownBy(() -> {
                            inquiry.canView(user);
                        })
                        .withMessage("다른 사용자가 작성한 문의글을 볼 수 없습니다.");
            }

            @Test
            @DisplayName("관리자라면 true를 반환한다.")
            void return_true_when_manager() {
                // given
                Inquiry inquiry = createSecretInquiryByAuthorId("tester1");
                Manager manager = createManagerByManagerId("manager1");

                // when, then
                assertThat(inquiry.canView(manager)).isTrue();
            }
        }

        @Nested
        @DisplayName("만약 비밀글 설정이 아닐때")
        class secret_false {

            @Test
            @DisplayName("동일한 사용자라면 true를 반환한다.")
            void return_true_when_same_user() {
                // given
                Inquiry inquiry = createNonSecretInquiryByAuthorId("tester1");
                User user = createUserByUserId("tester1");

                // when, then
                assertThat(inquiry.canView(user)).isTrue();
            }

            @Test
            @DisplayName("다른 사용자라면 ture를 반환한다.")
            void throw_exception_when_not_same_user() {
                // given
                Inquiry inquiry = createNonSecretInquiryByAuthorId("tester1");
                User user = createUserByUserId("tester2");

                // when, then
                assertThat(inquiry.canView(user)).isTrue();
            }

            @Test
            @DisplayName("관리자라면 true를 반환한다.")
            void return_true_when_manager() {
                // given
                Inquiry inquiry = createNonSecretInquiryByAuthorId("tester1");
                Manager manager = createManagerByManagerId("manager1");

                // when, then
                assertThat(inquiry.canView(manager)).isTrue();
            }
        }
    }
}
