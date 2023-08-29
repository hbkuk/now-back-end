package com.now.core.post.common.application;

import com.now.common.exception.ErrorType;
import com.now.config.annotations.ApplicationTest;
import com.now.config.fixtures.member.MemberFixture;
import com.now.config.fixtures.post.dto.PostReactionFixture;
import com.now.core.member.domain.Member;
import com.now.core.member.domain.MemberRepository;
import com.now.core.member.exception.InvalidMemberException;
import com.now.core.post.common.domain.repository.PostRepository;
import com.now.core.post.common.exception.CannotUpdateReactionException;
import com.now.core.post.common.exception.InvalidPostException;
import com.now.core.post.common.presentation.dto.PostReaction;
import com.now.core.post.common.presentation.dto.PostReactionResponse;
import com.now.core.post.common.presentation.dto.constants.Reaction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import static com.now.config.fixtures.member.MemberFixture.createMember;
import static com.now.config.fixtures.post.dto.PostReactionFixture.createPostReaction;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.Mockito.*;

@ApplicationTest
@DisplayName("게시글 서비스 객체는")
public class PostServiceTest {

    @Autowired
    private PostService postService;

    @MockBean
    private PostRepository postRepository;

    @MockBean
    private MemberRepository memberRepository;

    @Nested
    @DisplayName("게시물 반응 정보를 조회할 때")
    class Find_PostReaction {

        @Test
        @DisplayName("회원 정보가 없다면 InvalidMemberException을 던진다")
        void getPostReaction_when_member_null() {
            // given
            Long postIdx = 1L;
            String memberId = MemberFixture.SAMPLE_MEMBER_ID_1;
            when(memberRepository.findById(memberId)).thenReturn(null);

            // when, then
            assertThatExceptionOfType(InvalidMemberException.class)
                    .isThrownBy(() -> {
                        postService.getPostReaction(postIdx, memberId, false);
                    })
                    .withMessage(ErrorType.NOT_FOUND_MEMBER.getMessage());
        }

        @Test
        @DisplayName("게시물이 없다면 InvalidPostException을 던진다")
        void getPostReaction_when_post_null() {
            // given
            Long postIdx = 1L;
            String memberId = MemberFixture.SAMPLE_MEMBER_ID_1;
            Member member = createMember(memberId);
            when(memberRepository.findById(memberId)).thenReturn(member);
            when(postRepository.existPostByPostId(postIdx)).thenReturn(false);

            // when, then
            assertThatExceptionOfType(InvalidPostException.class)
                    .isThrownBy(() -> {
                        postService.getPostReaction(postIdx, memberId, false);
                    })
                    .withMessage(ErrorType.NOT_FOUND_POST.getMessage());
        }

        @Nested
        @DisplayName("반응에 대한 상세 정보 반환 여부를 false로 설정했을 때")
        class IsReactionDetails_false {

            @Test
            @DisplayName("기존에 저장된 반응 정보가 없다면 NOTTING으로 설정된 반응 정보가 반환된다")
            void getPostReaction_when_isReactionDetails_false_and_exist_reaction_null() {
                // given
                Long postIdx = 1L;
                Long memberIdx = 1L;
                String memberId = MemberFixture.SAMPLE_MEMBER_ID_1;
                Member member = createMember(memberIdx, memberId);
                PostReaction postReaction = PostReactionFixture.createPostReaction(postIdx, memberIdx);

                when(memberRepository.findById(memberId)).thenReturn(member);
                when(postRepository.existPostByPostId(postIdx)).thenReturn(true);
                when(postRepository.getPostReaction(postReaction)).thenReturn(null);

                // when
                PostReactionResponse postReactionResponse = postService.getPostReaction(postIdx, memberId, false);

                // then
                assertThat(postReactionResponse.getReaction()).isEqualTo(Reaction.NOTTING);

            }

            @Test
            @DisplayName("기존에 저장된 반응 정보가 있다면 해당 반응 정보가 반환된다")
            void getPostReaction_when_isReactionDetails_false_and_exist_reaction_not_null() {
                // given
                Long postIdx = 1L;
                Long memberIdx = 1L;
                String memberId = MemberFixture.SAMPLE_MEMBER_ID_1;
                Member member = createMember(memberIdx, memberId);
                PostReaction postReaction = PostReactionFixture.createPostReaction(postIdx, memberIdx);

                when(memberRepository.findById(memberId)).thenReturn(member);
                when(postRepository.existPostByPostId(postIdx)).thenReturn(true);
                when(postRepository.getPostReaction(postReaction)).thenReturn(PostReactionFixture.createPostReactionResponse(Reaction.LIKE));

                // when
                PostReactionResponse postReactionResponse = postService.getPostReaction(postIdx, memberId, false);

                // then
                assertThat(postReactionResponse.getReaction()).isEqualTo(Reaction.LIKE);

            }
        }

        @Nested
        @DisplayName("반응에 대한 상세 정보 반환 여부를 true로 설정했을 때")
        class IsReactionDetails_true {

            @Test
            @DisplayName("기존에 저장된 반응 정보가 없다면 NOTTING으로 설정된 반응 정보가 반환된다")
            void getPostReaction_when_isReactionDetails_true_and_exist_reaction_null() {
                // given
                Long postIdx = 1L;
                Long memberIdx = 1L;
                String memberId = MemberFixture.SAMPLE_MEMBER_ID_1;
                Member member = createMember(memberIdx, memberId);
                PostReaction postReaction = PostReactionFixture.createPostReaction(postIdx, memberIdx);

                when(memberRepository.findById(memberId)).thenReturn(member);
                when(postRepository.existPostByPostId(postIdx)).thenReturn(true);
                when(postRepository.getPostReactionDetails(postReaction)).thenReturn(PostReactionFixture.createPostReactionResponse(100, 5, null));

                // when
                PostReactionResponse postReactionResponse = postService.getPostReaction(postIdx, memberId, true);

                // then
                assertThat(postReactionResponse.getReaction()).isEqualTo(Reaction.NOTTING);

            }

            @Test
            @DisplayName("기존에 저장된 반응 정보가 있다면 해당 반응 정보가 반환된다")
            void getPostReaction_when_isReactionDetails_true_and_exist_reaction_not_null() {
                // given
                Long postIdx = 1L;
                Long memberIdx = 1L;
                String memberId = MemberFixture.SAMPLE_MEMBER_ID_1;
                Member member = createMember(memberIdx, memberId);
                PostReaction postReaction = PostReactionFixture.createPostReaction(postIdx, memberIdx);

                when(memberRepository.findById(memberId)).thenReturn(member);
                when(postRepository.existPostByPostId(postIdx)).thenReturn(true);
                when(postRepository.getPostReactionDetails(postReaction)).thenReturn(PostReactionFixture.createPostReactionResponse(100, 5, Reaction.LIKE));

                // when
                PostReactionResponse postReactionResponse = postService.getPostReaction(postIdx, memberId, true);

                // then
                assertThat(postReactionResponse.getReaction()).isEqualTo(Reaction.LIKE);

            }
        }
    }

    @Nested
    @DisplayName("게시물 반응 정보를 저장할 때")
    class Save_PostReaction {

        @Test
        @DisplayName("회원 정보가 없다면 InvalidMemberException을 던진다")
        void savePostReaction_when_member_null() {
            // given
            Long postIdx = 1L;
            String memberId = MemberFixture.SAMPLE_MEMBER_ID_1;
            when(memberRepository.findById(memberId)).thenReturn(null);

            // when, then
            assertThatExceptionOfType(InvalidMemberException.class)
                    .isThrownBy(() -> {
                        postService.savePostReaction(createPostReaction(postIdx, memberId, Reaction.LIKE));
                    })
                    .withMessage(ErrorType.NOT_FOUND_MEMBER.getMessage());
        }

        @Test
        @DisplayName("게시물이 없다면 InvalidPostException을 던진다")
        void savePostReaction_when_post_null() {
            // given
            Long postIdx = 1L;
            String memberId = MemberFixture.SAMPLE_MEMBER_ID_1;
            Member member = createMember(memberId);
            when(memberRepository.findById(memberId)).thenReturn(member);
            when(postRepository.existPostByPostId(postIdx)).thenReturn(false);

            // when, then
            assertThatExceptionOfType(InvalidPostException.class)
                    .isThrownBy(() -> {
                        postService.savePostReaction(createPostReaction(postIdx, memberId, Reaction.LIKE));
                    })
                    .withMessage(ErrorType.NOT_FOUND_POST.getMessage());
        }

        @Test
        @DisplayName("기존에 저장된 반응 정보가 없다면 반응 정보를 저장한다.")
        void savePostReaction_nonExistingPostReaction() {
            // given
            Long postIdx = 1L;
            Long memberIdx = 1L;
            String memberId = MemberFixture.SAMPLE_MEMBER_ID_1;
            Member member = createMember(memberIdx, memberId);
            when(memberRepository.findById(memberId)).thenReturn(member);
            when(postRepository.existPostByPostId(postIdx)).thenReturn(true);

            PostReaction postReaction = PostReactionFixture.createPostReaction(postIdx, memberIdx, null);
            PostReaction newPostReaction = PostReactionFixture.createPostReaction(postIdx, memberIdx, memberId, Reaction.LIKE);
            when(postRepository.getPostReaction(postReaction)).thenReturn(PostReactionFixture.createPostReactionResponse(Reaction.NOTTING));

            // when
            postService.savePostReaction(createPostReaction(postIdx, memberId, Reaction.LIKE));

            // then
            verify(postRepository, times(1)).incrementLikeCount(newPostReaction.getPostIdx());
            verify(postRepository, times(1)).savePostReaction(newPostReaction);
        }

        @Test
        @DisplayName("기존에 저장된 반응 정보가 있다면 해당 반응 정보를 저장한다.")
        void savePostReaction_existingPostReaction_1() {
            // given
            Long postIdx = 1L;
            Long memberIdx = 1L;
            String memberId = MemberFixture.SAMPLE_MEMBER_ID_1;
            Member member = createMember(memberIdx, memberId);
            when(memberRepository.findById(memberId)).thenReturn(member);
            when(postRepository.existPostByPostId(postIdx)).thenReturn(true);

            PostReaction postReaction = PostReactionFixture.createPostReaction(postIdx, memberIdx, null);
            PostReaction newPostReaction = PostReactionFixture.createPostReaction(postIdx, memberIdx, memberId, Reaction.LIKE);
            when(postRepository.getPostReaction(postReaction)).thenReturn(PostReactionFixture.createPostReactionResponse(Reaction.DISLIKE));

            // when
            postService.savePostReaction(createPostReaction(postIdx, memberId, Reaction.LIKE));

            // then
            verify(postRepository, times(1)).decrementDislikeCount(newPostReaction.getPostIdx());
            verify(postRepository, times(1)).incrementLikeCount(newPostReaction.getPostIdx());
            verify(postRepository, times(1)).updatePostReaction(newPostReaction);
        }

        @Test
        @DisplayName("기존에 저장된 반응 정보가 있다면 해당 반응 정보를 저장한다.")
        void savePostReaction_existingPostReaction_2() {
            // given
            Long postIdx = 1L;
            Long memberIdx = 1L;
            String memberId = MemberFixture.SAMPLE_MEMBER_ID_1;
            Member member = createMember(memberIdx, memberId);
            when(memberRepository.findById(memberId)).thenReturn(member);
            when(postRepository.existPostByPostId(postIdx)).thenReturn(true);

            PostReaction postReaction = PostReactionFixture.createPostReaction(postIdx, memberIdx, null);
            PostReaction newPostReaction = PostReactionFixture.createPostReaction(postIdx, memberIdx, memberId, Reaction.UNDISLIKE);
            when(postRepository.getPostReaction(postReaction)).thenReturn(PostReactionFixture.createPostReactionResponse(Reaction.DISLIKE));

            // when
            postService.savePostReaction(createPostReaction(postIdx, memberId, Reaction.UNDISLIKE));

            // then
            verify(postRepository, times(1)).decrementDislikeCount(newPostReaction.getPostIdx());
            verify(postRepository, times(1)).updatePostReaction(newPostReaction);
        }

        @Test
        @DisplayName("기존에 저장된 반응 정보가 있을 때 업데이트할 수 없다면 CannotUpdateReactionException을 던진다")
        void savePostReaction_existingPostReaction_throw_cannotUpdateReactionException() {
            // given
            Long postIdx = 1L;
            Long memberIdx = 1L;
            String memberId = MemberFixture.SAMPLE_MEMBER_ID_1;
            Member member = createMember(memberIdx, memberId);
            when(memberRepository.findById(memberId)).thenReturn(member);
            when(postRepository.existPostByPostId(postIdx)).thenReturn(true);

            PostReaction postReaction = PostReactionFixture.createPostReaction(postIdx, memberIdx, null);
            when(postRepository.getPostReaction(postReaction)).thenReturn(PostReactionFixture.createPostReactionResponse(Reaction.DISLIKE));

            // when, thenm
            assertThatExceptionOfType(CannotUpdateReactionException.class)
                    .isThrownBy(() -> {
                        postService.savePostReaction(createPostReaction(postIdx, memberId, Reaction.UNLIKE));
                    }).withMessageMatching(ErrorType.CAN_NOT_UPDATE_REACTION.getMessage());
        }
    }
}
