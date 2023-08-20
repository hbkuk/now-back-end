package com.now.core.post.community.domain;

import com.now.config.fixtures.comment.CommentFixture;
import com.now.config.fixtures.member.MemberFixture;
import com.now.config.fixtures.post.CommunityFixture;
import com.now.core.comment.domain.Comment;
import com.now.core.member.domain.Member;
import com.now.core.post.community.domain.Community;
import com.now.core.post.common.exception.CannotDeletePostException;
import com.now.core.post.common.exception.CannotUpdatePostException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

@DisplayName("Community 도메인 객체")
public class CommunityTest {

    @Nested
    @DisplayName("canUpdate 메서드는")
    class CanUpdate_of {

        @Test
        @DisplayName("Member 객체가 매개변수로 전달될 때, 동일한 회원이라면 true를 반환한다.")
        void return_true_when_same_user() {
            // given
            Member member = MemberFixture.createMember("tester1");
            Community community = CommunityFixture.createCommunity("tester1");

            // when, then
            assertThat(community.canUpdate(member)).isTrue();
        }

        @Test
        @DisplayName("Member 객체가 매개변수로 전달될 때, 다른 회원이라면 CannotUpdatePostException을 던진다.")
        void throw_exception_when_not_same_user() {
            // given
            Member member = MemberFixture.createMember("tester1");
            Community community = CommunityFixture.createCommunity("tester2");

            // when, then
            assertThatExceptionOfType(CannotUpdatePostException.class)
                    .isThrownBy(() -> {
                        community.canUpdate(member);
                    })
                    .withMessageMatching("다른 회원이 작성한 게시글을 수정할 수 없습니다.");
        }
    }

    @Nested
    @DisplayName("canDelete 메서드는")
    class CanDelete_of {

        @Nested
        @DisplayName("Member 객체가 매개변수로 전달될 때,")
        class SameUser {

            @Test
            @DisplayName("동일한 회원이면서, 댓글이 하나도 없을 경우 true를 반환한다.")
            void return_true_when_nothing_comment() {
                // given
                Member member = MemberFixture.createMember("tester1");
                Community community = CommunityFixture.createCommunity("tester1");

                // when, then
                assertThat(community.canDelete(member, new ArrayList<Comment>())).isTrue();

            }

            @Test
            @DisplayName("동일한 회원이면서, 댓글 작성자도 같다면 true를 반환한다.")
            void return_true_when_same_comment_author() {
                // given
                Member member = MemberFixture.createMember("tester1");
                Community community = CommunityFixture.createCommunity("tester1");
                List<Comment> comments = Arrays.asList(CommentFixture.createCommentByMemberId("tester1"));

                // when, then
                assertThat(community.canDelete(member, comments)).isTrue();
            }

            @Test
            @DisplayName("댓글 작성자가 다르다면 CanDeletePostException을 던진다.")
            void throw_exception_when_not_same_comment_author() {
                // given
                Member member = MemberFixture.createMember("tester1");
                Community community = CommunityFixture.createCommunity("tester1");
                List<Comment> comments = Arrays.asList(CommentFixture.createCommentByMemberId("tester2"));

                // when, then
                assertThatExceptionOfType(CannotDeletePostException.class)
                        .isThrownBy(() -> {
                            community.canDelete(member, comments);
                        })
                        .withMessageMatching("다른 회원이 작성한 댓글이 있으므로 해당 게시글을 삭제할 수 없습니다.");
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
                Community community = CommunityFixture.createCommunity("tester2");

                // when, then
                assertThatExceptionOfType(CannotDeletePostException.class)
                        .isThrownBy(() -> {
                            community.canDelete(member, new ArrayList<Comment>());
                        })
                        .withMessageMatching("다른 회원이 작성한 게시글을 삭제할 수 없습니다.");
            }

            @Test
            @DisplayName("댓글이 있을 경우 CanDeletePostException을 던진다.")
            void throw_exception_when_exist_comment() {
                // given
                Member member = MemberFixture.createMember("tester1");
                Community community = CommunityFixture.createCommunity("tester2");
                List<Comment> comments = Arrays.asList(CommentFixture.createCommentByMemberId("tester2"));

                // when, then
                assertThatExceptionOfType(CannotDeletePostException.class)
                        .isThrownBy(() -> {
                            community.canDelete(member, comments);
                        })
                        .withMessageMatching("다른 회원이 작성한 게시글을 삭제할 수 없습니다.");
            }

            @Test
            @DisplayName("댓글이 같은 회원일 경우 CanDeletePostException을 던진다.")
            void throw_exception_when_same_comment_author() {
                // given
                Member member = MemberFixture.createMember("tester1");
                Community community = CommunityFixture.createCommunity("tester2");
                List<Comment> comments = Arrays.asList(CommentFixture.createCommentByMemberId("tester1"));

                // when, then
                assertThatExceptionOfType(CannotDeletePostException.class)
                        .isThrownBy(() -> {
                            community.canDelete(member, comments);
                        })
                        .withMessageMatching("다른 회원이 작성한 게시글을 삭제할 수 없습니다.");
            }
        }
    }

}
