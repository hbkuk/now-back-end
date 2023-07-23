package com.now.core.post.domain;

import com.now.config.fixtures.member.MemberFixture;
import com.now.core.member.domain.Member;
import com.now.core.post.exception.CannotViewInquiryException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.now.config.fixtures.post.InquiryFixture.createNonSecretInquiry;
import static com.now.config.fixtures.post.InquiryFixture.createSecretInquiry;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class InquiryTest {

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
