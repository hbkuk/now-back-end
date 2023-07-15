package com.now.domain.post;

import com.now.core.authentication.constants.Authority;
import com.now.core.member.domain.Member;
import com.now.core.post.domain.Inquiry;
import com.now.core.post.exception.CannotViewInquiryException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static com.now.domain.user.MemberTest.createMember;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class InquiryTest {
    public static Inquiry createSecretInquiry(String authorId) {
        return Inquiry.builder()
                .postIdx(1L)
                .title("제목")
                .memberId(authorId)
                .regDate(LocalDateTime.now())
                .modDate(LocalDateTime.now())
                .content("내용")
                .viewCount(0)
                .likeCount(0)
                .dislikeCount(0)
                .secret(true)
                .build();
    }

    public static Inquiry createNonSecretInquiryByAuthorId(String authorId) {
        return Inquiry.builder()
                .postIdx(1L)
                .title("제목")
                .answerManagerIdx(authorId)
                .regDate(LocalDateTime.now())
                .modDate(LocalDateTime.now())
                .content("내용")
                .viewCount(0)
                .likeCount(0)
                .dislikeCount(0)
                .secret(false)
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
            void return_true_when_same_member() {
                // given
                Inquiry inquiry = createSecretInquiry("tester1");
                Member member = createMember("tester1");

                // when, then
                assertThat(inquiry.canView(Authority.MEMBER, member)).isTrue();
            }

            @Test
            @DisplayName("다른 사용자라면 CannotViewInquiryException을 던진다.")
            void throw_exception_when_not_same_member() {
                // given
                Inquiry inquiry = createSecretInquiry("tester1");
                Member member = createMember("tester2");

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
                Inquiry inquiry = createNonSecretInquiryByAuthorId("tester1");
                Member member = createMember("tester1");

                // when, then
                assertThat(inquiry.canView(Authority.MEMBER, member)).isTrue();
            }

            @Test
            @DisplayName("다른 사용자라면 ture를 반환한다.")
            void throw_exception_when_not_same_member() {
                // given
                Inquiry inquiry = createNonSecretInquiryByAuthorId("tester1");
                Member member = createMember("tester2");

                // when, then
                assertThat(inquiry.canView(Authority.MEMBER, member)).isTrue();
            }
        }
    }
}
