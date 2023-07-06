package com.now.domain;

import com.now.domain.comment.Comment;
import com.now.domain.manager.Manager;
import com.now.domain.post.Post;
import com.now.domain.user.User;
import com.now.exception.CannotDeletePostException;
import com.now.exception.CannotUpdatePostException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.now.domain.CommentTest.createCommentByAuthorId;
import static com.now.domain.UserTest.createUserByUserId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class PostTest {
    public static Post createPostByAuthorId(String authorId) {
        return Post.builder()
                .postIdx(1L)
                .subCodeIdx(3)
                .subCodeName(3)
                .title("제목")
                .authorId(authorId)
                .regDate(LocalDateTime.now())
                .modDate(LocalDateTime.now())
                .content("내용")
                .viewCount(0)
                .likeCount(0)
                .dislikeCount(0)
                .isCurrentUserPost(false)
                .build();
    }

    @Nested
    @DisplayName("canUpdate 메서드는")
    class CanUpdate_of {

        @Test
        @DisplayName("만약 관리자라면 true를 반환한다.")
        void return_true_when_manager() {
            // given
            Manager manager = ManagerTest.createManagerByManagerId("manager1");
            Post post = createPostByAuthorId("tester1");

            // when, then
            assertThat(post.canUpdate(manager)).isTrue();
        }

        @Test
        @DisplayName("만약 동일한 사용자라면 true를 반환한다.")
        void return_true_when_same_user() {
            // given
            User user = createUserByUserId("tester1");
            Post post = createPostByAuthorId("tester1");

            // when, then
            assertThat(post.canUpdate(user)).isTrue();
        }

        @Test
        @DisplayName("만약 다른 사용자라면 CannotUpdatePostException을 던진다.")
        void throw_exception_when_not_same_user() {
            // given
            User user = createUserByUserId("tester1");
            Post post = createPostByAuthorId("tester2");

            // when, then
            assertThatExceptionOfType(CannotUpdatePostException.class)
                    .isThrownBy(() -> {
                        post.canUpdate(user);
                    })
                    .withMessageMatching("다른 사용자가 작성한 게시글을 수정할 수 없습니다.");
        }
    }

    @Nested
    @DisplayName("canDelete 메서드는")
    class CanDelete_of {

        @Test
        @DisplayName("만약 관리자라면 항상 true를 반환한다.")
        void return_true_when_manager() {
            // given
            Manager manager = ManagerTest.createManagerByManagerId("manager1");
            Post post = createPostByAuthorId("tester1");
            List<Comment> comments = Arrays.asList(createCommentByAuthorId("tester1"));

            // when, then
            assertThat(post.canDelete(manager, new ArrayList<Comment>())).isTrue();
            assertThat(post.canDelete(manager, comments)).isTrue();
        }

        @Nested
        @DisplayName("만약 동일한 사용자이면서")
        class SameUser {

            @Test
            @DisplayName("댓글이 하나도 없을 경우 true를 반환한다.")
            void return_true_when_nothing_comment() {
                // given
                User user = createUserByUserId("tester1");
                Post post = createPostByAuthorId("tester1");

                // when, then
                assertThat(post.canDelete(user, new ArrayList<Comment>())).isTrue();

            }

            @Test
            @DisplayName("댓글 작성자도 같다면 true를 반환한다.")
            void return_true_when_same_comment_author() {
                // given
                User user = createUserByUserId("tester1");
                Post post = createPostByAuthorId("tester1");
                List<Comment> comments = Arrays.asList(createCommentByAuthorId("tester1"));

                // when, then
                assertThat(post.canDelete(user, comments)).isTrue();

            }

            @Test
            @DisplayName("댓글 작성자가 다르다면 CanDeletePostException을 던진다.")
            void throw_exception_when_not_same_comment_author() {
                // given
                User user = createUserByUserId("tester1");
                Post post = createPostByAuthorId("tester1");
                List<Comment> comments = Arrays.asList(createCommentByAuthorId("tester2"));

                // when, then
                assertThatExceptionOfType(CannotDeletePostException.class)
                        .isThrownBy(() -> {
                            post.canDelete(user, comments);
                        })
                        .withMessageMatching("다른 사용자가 작성한 댓글이 있으므로 해당 게시글을 삭제할 수 없습니다.");
            }
        }

        @Nested
        @DisplayName("만약 다른 사용자이면서")
        class NotSameUser {

            @Test
            @DisplayName("댓글이 하나도 없을 경우 CanDeletePostException을 던진다.")
            void throw_exception_when_nothing_comment() {
                // given
                User user = createUserByUserId("tester1");
                Post post = createPostByAuthorId("tester2");

                // when, then
                assertThatExceptionOfType(CannotDeletePostException.class)
                        .isThrownBy(() -> {
                            post.canDelete(user, new ArrayList<Comment>());
                        })
                        .withMessageMatching("다른 사용자가 작성한 게시글을 삭제할 수 없습니다.");
            }

            @Test
            @DisplayName("댓글이 있을 경우 CanDeletePostException을 던진다.")
            void throw_exception_when_exist_comment() {
                // given
                User user = createUserByUserId("tester1");
                Post post = createPostByAuthorId("tester2");
                List<Comment> comments = Arrays.asList(createCommentByAuthorId("tester2"));

                // when, then
                assertThatExceptionOfType(CannotDeletePostException.class)
                        .isThrownBy(() -> {
                            post.canDelete(user, comments);
                        })
                        .withMessageMatching("다른 사용자가 작성한 게시글을 삭제할 수 없습니다.");
            }

            @Test
            @DisplayName("댓글이 같은 사용자일 경우 CanDeletePostException을 던진다.")
            void throw_exception_when_same_comment_author() {
                // given
                User user = createUserByUserId("tester1");
                Post post = createPostByAuthorId("tester2");
                List<Comment> comments = Arrays.asList(createCommentByAuthorId("tester1"));

                // when, then
                assertThatExceptionOfType(CannotDeletePostException.class)
                        .isThrownBy(() -> {
                            post.canDelete(user, comments);
                        })
                        .withMessageMatching("다른 사용자가 작성한 게시글을 삭제할 수 없습니다.");
            }
        }
    }
}
