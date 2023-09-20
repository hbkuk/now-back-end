package com.now.core.post.common.domain.repository;

import com.now.config.annotations.RepositoryTest;
import com.now.core.admin.authentication.domain.Manager;
import com.now.core.admin.authentication.domain.ManagerRepository;
import com.now.core.attachment.domain.AttachmentRepository;
import com.now.core.category.domain.constants.Category;
import com.now.core.category.domain.constants.PostGroup;
import com.now.core.comment.domain.CommentRepository;
import com.now.core.member.domain.Member;
import com.now.core.member.domain.MemberRepository;
import com.now.core.post.common.presentation.dto.Condition;
import com.now.core.post.common.presentation.dto.PostReaction;
import com.now.core.post.common.presentation.dto.PostReactionResponse;
import com.now.core.post.common.presentation.dto.Posts;
import com.now.core.post.common.presentation.dto.constants.Reaction;
import com.now.core.post.common.presentation.dto.constants.Sort;
import com.now.core.post.community.domain.Community;
import com.now.core.post.community.domain.repository.CommunityRepository;
import com.now.core.post.inquiry.domain.Inquiry;
import com.now.core.post.inquiry.domain.repository.InquiryRepository;
import com.now.core.post.notice.domain.Notice;
import com.now.core.post.notice.domain.repository.NoticeRepository;
import com.now.core.post.photo.domain.Photo;
import com.now.core.post.photo.domain.repository.PhotoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.now.config.fixtures.manager.ManagerFixture.*;
import static com.now.config.fixtures.member.MemberFixture.*;
import static com.now.config.fixtures.post.CommunityFixture.createCommunityForSave;
import static com.now.config.fixtures.post.InquiryFixture.createInquiryForSave;
import static com.now.config.fixtures.post.NoticeFixture.createNoticeForSave;
import static com.now.config.fixtures.post.PhotoFixture.createPhotoForSave;
import static com.now.config.fixtures.post.dto.ConditionFixture.createCondition;
import static com.now.config.fixtures.post.dto.PostReactionFixture.createPostReaction;
import static org.assertj.core.api.Assertions.assertThat;

@RepositoryTest
@DisplayName("게시글 레포지토리")
class PostRepositoryTest {

    @Autowired
    protected PostRepository postRepository;

    @Autowired
    protected NoticeRepository noticeRepository;

    @Autowired
    protected CommunityRepository communityRepository;

    @Autowired
    protected PhotoRepository photoRepository;

    @Autowired
    protected InquiryRepository inquiryRepository;

    @Autowired
    protected ManagerRepository managerRepository;

    @Autowired
    protected MemberRepository memberRepository;

    @Autowired
    protected CommentRepository commentRepository;

    @Autowired
    protected AttachmentRepository attachmentRepository;

    private void randomIncreaseViewCount(PostRepository postRepository, List<Long> postIndexes, int[] viewCounts) {
        for (int i = 0; i < postIndexes.size(); i++) {
            for (int j = 0; j < viewCounts[i]; j++) {
                postRepository.incrementViewCount(postIndexes.get(i));
            }
        }
    }

    @Test
    @DisplayName("게시물의 조회수가 정상적으로 증가한다")
    void incrementViewCount() {
        // given
        List<Member> members = Arrays.asList(
                createMember(MEMBER1_ID, MEMBER1_NAME, MEMBER1_NICKNAME),
                createMember(MEMBER2_ID, MEMBER2_NAME, MEMBER2_NICKNAME),
                createMember(MEMBER3_ID, MEMBER3_NAME, MEMBER3_NICKNAME),
                createMember(MEMBER4_ID, MEMBER4_NAME, MEMBER4_NICKNAME),
                createMember(MEMBER5_ID, MEMBER5_NAME, MEMBER5_NICKNAME));

        List<Community> communities = members.stream()
                .map(member -> createCommunityForSave(member.getId())).collect(Collectors.toList());

        // when
        members.forEach(member -> memberRepository.saveMember(member));
        communities.forEach(community -> communityRepository.saveCommunity(community));

        int[] expectedViewCount = new int[]{2, 3, 1, 2, 1};
        List<Long> actualPostIndexes = communities.stream()
                .map(Community::getPostIdx)
                .collect(Collectors.toList());
        randomIncreaseViewCount(postRepository, actualPostIndexes, expectedViewCount);

        // then
        assertThat(communityRepository.findCommunity(1L).getViewCount()).isEqualTo(expectedViewCount[0]);
        assertThat(communityRepository.findCommunity(2L).getViewCount()).isEqualTo(expectedViewCount[1]);
        assertThat(communityRepository.findCommunity(3L).getViewCount()).isEqualTo(expectedViewCount[2]);
        assertThat(communityRepository.findCommunity(4L).getViewCount()).isEqualTo(expectedViewCount[3]);
        assertThat(communityRepository.findCommunity(5L).getViewCount()).isEqualTo(expectedViewCount[4]);
    }

    @Nested
    @DisplayName("모든 게시물을 찾을 때")
    class FindAll {

        @Test
        @DisplayName("전달받은 게시글의 최대 개수(maxNumberOfPosts)만큼 조회한다")
        void findAllPosts_maxNumberOfPosts() {
            // given
            List<Manager> managers = Arrays.asList(
                    createManager(MANAGER1_ID, MANAGER1_NAME, MANAGER1_NICKNAME),
                    createManager(MANAGER2_ID, MANAGER2_NAME, MANAGER2_NICKNAME),
                    createManager(MANAGER3_ID, MANAGER3_NAME, MANAGER3_NICKNAME),
                    createManager(MANAGER4_ID, MANAGER4_NAME, MANAGER4_NICKNAME),
                    createManager(MANAGER5_ID, MANAGER5_NAME, MANAGER5_NICKNAME));

            List<Member> members = Arrays.asList(
                    createMember(MEMBER1_ID, MEMBER1_NAME, MEMBER1_NICKNAME),
                    createMember(MEMBER2_ID, MEMBER2_NAME, MEMBER2_NICKNAME),
                    createMember(MEMBER3_ID, MEMBER3_NAME, MEMBER3_NICKNAME),
                    createMember(MEMBER4_ID, MEMBER4_NAME, MEMBER4_NICKNAME),
                    createMember(MEMBER5_ID, MEMBER5_NAME, MEMBER5_NICKNAME));

            List<Notice> notPinnedNotices = managers.stream()
                    .map(manager -> createNoticeForSave(Category.EVENT, manager.getId(), false))
                    .collect(Collectors.toList());

            List<Community> communities = members.stream()
                    .map(member -> createCommunityForSave(member.getId())).collect(Collectors.toList());

            List<Photo> photos = members.stream()
                    .map(member -> createPhotoForSave(member.getId())).collect(Collectors.toList());

            List<Inquiry> inquiries = members.stream()
                    .map(member -> createInquiryForSave(member.getId())).collect(Collectors.toList());

            Condition condition = createCondition(Sort.LATEST, 3);

            // when
            managers.forEach(manager -> managerRepository.saveManager(manager));
            members.forEach(member -> memberRepository.saveMember(member));

            notPinnedNotices.forEach(notice -> noticeRepository.saveNotice(notice));
            communities.forEach(community -> communityRepository.saveCommunity(community));
            photos.forEach(photo -> photoRepository.savePhoto(photo));
            inquiries.forEach(inquiry -> {
                inquiryRepository.savePost(inquiry);
                inquiryRepository.saveInquirySecretSetting(inquiry);
            });

            List<Posts> actualPosts = postRepository.findAllPosts(condition);

            // then
            assertThat(actualPosts).hasSize(condition.getMaxNumberOfPosts() * 4);
        }
    }

    @Nested
    @DisplayName("특정 게시물의 개수를 찾을 때")
    class FindTotalPostCount {

        @Test
        @DisplayName("조건(Condition)에 따른 개수를 반환한다")
        void findTotalPostCount_condition() {
            // given
            List<Manager> managers = Arrays.asList(
                    createManager(MANAGER1_ID, MANAGER1_NAME, MANAGER1_NICKNAME),
                    createManager(MANAGER2_ID, MANAGER2_NAME, MANAGER2_NICKNAME),
                    createManager(MANAGER3_ID, MANAGER3_NAME, MANAGER3_NICKNAME),
                    createManager(MANAGER4_ID, MANAGER4_NAME, MANAGER4_NICKNAME),
                    createManager(MANAGER5_ID, MANAGER5_NAME, MANAGER5_NICKNAME));

            List<Member> members = Arrays.asList(
                    createMember(MEMBER1_ID, MEMBER1_NAME, MEMBER1_NICKNAME),
                    createMember(MEMBER2_ID, MEMBER2_NAME, MEMBER2_NICKNAME),
                    createMember(MEMBER3_ID, MEMBER3_NAME, MEMBER3_NICKNAME),
                    createMember(MEMBER4_ID, MEMBER4_NAME, MEMBER4_NICKNAME),
                    createMember(MEMBER5_ID, MEMBER5_NAME, MEMBER5_NICKNAME));

            List<Notice> expectedNotPinnedEventNotices = managers.stream()
                    .map(manager -> createNoticeForSave(Category.EVENT, manager.getId(), false))
                    .collect(Collectors.toList());
            List<Notice> expectedNotPinnedNewsNotices = managers.stream()
                    .map(manager -> createNoticeForSave(Category.NEWS, manager.getId(), false))
                    .collect(Collectors.toList());

            List<Community> expectedLifeStyleCommunities = members.stream()
                    .map(member -> createCommunityForSave(member.getId(), Category.LIFESTYLE)).collect(Collectors.toList());
            List<Community> expectedCommunityStudyCommunities = members.stream()
                    .map(member -> createCommunityForSave(member.getId(), Category.COMMUNITY_STUDY)).collect(Collectors.toList());

            List<Photo> expectedArtworkPhotos = members.stream()
                    .map(member -> createPhotoForSave(member.getId(), Category.ARTWORK)).collect(Collectors.toList());
            List<Photo> expectedDailyLifePhotos = members.stream()
                    .map(member -> createPhotoForSave(member.getId(), Category.DAILY_LIFE)).collect(Collectors.toList());

            List<Inquiry> expectedServiceInquiries = members.stream()
                    .map(member -> createInquiryForSave(member.getId(), Category.SERVICE)).collect(Collectors.toList());
            List<Inquiry> expectedTechnologyInquiries = members.stream()
                    .map(member -> createInquiryForSave(member.getId(), Category.TECHNOLOGY)).collect(Collectors.toList());

            Condition eventNoticeCondition = createCondition(PostGroup.NOTICE, Category.EVENT);
            Condition lifestyleCommunityCondition = createCondition(PostGroup.COMMUNITY, Category.LIFESTYLE);
            Condition artworkPhotoCondition = createCondition(PostGroup.PHOTO, Category.ARTWORK);
            Condition serviceInquiryCondition = createCondition(PostGroup.INQUIRY, Category.SERVICE);

            // when
            managers.forEach(manager -> managerRepository.saveManager(manager));
            members.forEach(member -> memberRepository.saveMember(member));

            expectedNotPinnedEventNotices.forEach(notice -> noticeRepository.saveNotice(notice));
            expectedNotPinnedNewsNotices.forEach(notice -> noticeRepository.saveNotice(notice));
            expectedLifeStyleCommunities.forEach(community -> communityRepository.saveCommunity(community));
            expectedCommunityStudyCommunities.forEach(community -> communityRepository.saveCommunity(community));
            expectedArtworkPhotos.forEach(photo -> photoRepository.savePhoto(photo));
            expectedDailyLifePhotos.forEach(photo -> photoRepository.savePhoto(photo));
            expectedServiceInquiries.forEach(inquiry -> {
                inquiryRepository.savePost(inquiry);
                inquiryRepository.saveInquirySecretSetting(inquiry);
            });
            expectedTechnologyInquiries.forEach(inquiry -> {
                inquiryRepository.savePost(inquiry);
                inquiryRepository.saveInquirySecretSetting(inquiry);
            });

            Long actualEventNoticeCount = postRepository.findTotalPostCount(eventNoticeCondition);
            Long actualLifestyleCommunityCount = postRepository.findTotalPostCount(lifestyleCommunityCondition);
            Long actualArtworkPhotoCount = postRepository.findTotalPostCount(artworkPhotoCondition);
            Long actualServiceInquiryCount = postRepository.findTotalPostCount(serviceInquiryCondition);

            assertThat(actualEventNoticeCount).isEqualTo(expectedNotPinnedEventNotices.size());
            assertThat(actualLifestyleCommunityCount).isEqualTo(expectedLifeStyleCommunities.size());
            assertThat(actualArtworkPhotoCount).isEqualTo(expectedArtworkPhotos.size());
            assertThat(actualServiceInquiryCount).isEqualTo(expectedServiceInquiries.size());
        }
    }

    @Nested
    @DisplayName("게시글의 반응 정보")
    class getReaction {

        public List<Member> members;
        public List<Community> communities;
        public List<PostReaction> postLikeReactions;

        @BeforeEach
        void setUp() {
            // given
            members = Arrays.asList(
                    createMember(MEMBER1_ID, MEMBER1_NAME, MEMBER1_NICKNAME),
                    createMember(MEMBER2_ID, MEMBER2_NAME, MEMBER2_NICKNAME),
                    createMember(MEMBER3_ID, MEMBER3_NAME, MEMBER3_NICKNAME),
                    createMember(MEMBER4_ID, MEMBER4_NAME, MEMBER4_NICKNAME),
                    createMember(MEMBER5_ID, MEMBER5_NAME, MEMBER5_NICKNAME));
            communities = members.stream()
                    .map(member -> createCommunityForSave(member.getId())).collect(Collectors.toList());
            postLikeReactions = Arrays.asList(
                    createPostReaction(1L, 1L, Reaction.LIKE),
                    createPostReaction(1L, 2L, Reaction.LIKE),
                    createPostReaction(1L, 3L, Reaction.LIKE),

                    createPostReaction(2L, 2L, Reaction.LIKE),
                    createPostReaction(2L, 1L, Reaction.LIKE),

                    createPostReaction(3L, 3L, Reaction.LIKE),

                    createPostReaction(4L, 4L, Reaction.LIKE),
                    createPostReaction(4L, 1L, Reaction.LIKE),

                    createPostReaction(5L, 5L, Reaction.LIKE)
            );

            members.forEach(member -> memberRepository.saveMember(member));
            communities.forEach(community -> communityRepository.saveCommunity(community));
            postLikeReactions.forEach(postReaction -> postRepository.savePostReaction(postReaction));
            postLikeReactions.forEach(postReaction -> postRepository.incrementLikeCount(postReaction.getPostIdx()));
        }

        @Test
        @DisplayName("게시물의 반응 정보만 조회한다")
        void getPostReaction() {
            List<PostReactionResponse> postReactionResponses = postLikeReactions.stream()
                    .map(postReaction -> postRepository.getPostReaction(postReaction))
                    .collect(Collectors.toList());

            boolean hasAnyLikeReactions = postReactionResponses.stream()
                    .allMatch(postReactionResponse -> postReactionResponse.getReaction() == Reaction.LIKE);
            boolean hasAnyNullLikeCount = postReactionResponses.stream()
                    .allMatch(postReactionResponse -> postReactionResponse.getLikeCount() == null);
            boolean hasAnyNullDislikeCount = postReactionResponses.stream()
                    .allMatch(postReactionResponse -> postReactionResponse.getDislikeCount() == null);

            assertThat(hasAnyLikeReactions).isTrue();
            assertThat(hasAnyNullLikeCount).isTrue();
            assertThat(hasAnyNullDislikeCount).isTrue();
        }

        @Test
        @DisplayName("게시물의 반응 정보와 상세 내용까지 조회한다")
        void getPostReactionDetails() {
            List<PostReactionResponse> actualPostReactionResponses = postLikeReactions.stream()
                    .map(postReaction -> postRepository.getPostReactionDetails(postReaction))
                    .collect(Collectors.toList());

            boolean hasAnyLikeReactions = actualPostReactionResponses.stream()
                    .allMatch(postReactionResponse -> postReactionResponse.getReaction() == Reaction.LIKE);
            boolean hasAnyNotNullLikeCount = actualPostReactionResponses.stream()
                    .allMatch(postReactionResponse -> postReactionResponse.getLikeCount() != null);
            boolean hasAnyNotNullDislikeCount = actualPostReactionResponses.stream()
                    .allMatch(postReactionResponse -> postReactionResponse.getDislikeCount() != null);

            List<Integer> expectedLikeCounts = postLikeReactions.stream()
                    .collect(Collectors.groupingBy(PostReaction::getPostIdx))
                    .values()
                    .stream()
                    .map(List::size)
                    .collect(Collectors.toList());

            assertThat(hasAnyLikeReactions).isTrue();
            assertThat(hasAnyNotNullLikeCount).isTrue();
            assertThat(hasAnyNotNullDislikeCount).isTrue();

            assertThat(actualPostReactionResponses.get(0).getLikeCount()).isEqualTo(expectedLikeCounts.get(0));
            assertThat(actualPostReactionResponses.get(1).getLikeCount()).isEqualTo(expectedLikeCounts.get(0));
            assertThat(actualPostReactionResponses.get(2).getLikeCount()).isEqualTo(expectedLikeCounts.get(0));

            assertThat(actualPostReactionResponses.get(3).getLikeCount()).isEqualTo(expectedLikeCounts.get(1));
            assertThat(actualPostReactionResponses.get(4).getLikeCount()).isEqualTo(expectedLikeCounts.get(1));

            assertThat(actualPostReactionResponses.get(5).getLikeCount()).isEqualTo(expectedLikeCounts.get(2));

            assertThat(actualPostReactionResponses.get(6).getLikeCount()).isEqualTo(expectedLikeCounts.get(3));
            assertThat(actualPostReactionResponses.get(7).getLikeCount()).isEqualTo(expectedLikeCounts.get(3));

            assertThat(actualPostReactionResponses.get(8).getLikeCount()).isEqualTo(expectedLikeCounts.get(4));
        }
    }

    @Nested
    @DisplayName("게시글 좋아요의 증가")
    class IncrementLikeCount {

        public List<Member> members;
        public List<Community> communities;
        public List<PostReaction> postLikeReactions;

        @BeforeEach
        void setUp() {
            // given
            members = Arrays.asList(
                    createMember(MEMBER1_ID, MEMBER1_NAME, MEMBER1_NICKNAME),
                    createMember(MEMBER2_ID, MEMBER2_NAME, MEMBER2_NICKNAME),
                    createMember(MEMBER3_ID, MEMBER3_NAME, MEMBER3_NICKNAME),
                    createMember(MEMBER4_ID, MEMBER4_NAME, MEMBER4_NICKNAME),
                    createMember(MEMBER5_ID, MEMBER5_NAME, MEMBER5_NICKNAME));
            communities = members.stream()
                    .map(member -> createCommunityForSave(member.getId())).collect(Collectors.toList());
            postLikeReactions = Arrays.asList(
                    createPostReaction(1L, 1L, Reaction.LIKE),
                    createPostReaction(1L, 2L, Reaction.LIKE),
                    createPostReaction(1L, 3L, Reaction.LIKE),

                    createPostReaction(2L, 2L, Reaction.LIKE),
                    createPostReaction(2L, 1L, Reaction.LIKE),

                    createPostReaction(3L, 3L, Reaction.LIKE),

                    createPostReaction(4L, 4L, Reaction.LIKE),
                    createPostReaction(4L, 1L, Reaction.LIKE),

                    createPostReaction(5L, 5L, Reaction.LIKE)
            );

            members.forEach(member -> memberRepository.saveMember(member));
            communities.forEach(community -> communityRepository.saveCommunity(community));
            postLikeReactions.forEach(postReaction -> postRepository.savePostReaction(postReaction));
            postLikeReactions.forEach(postReaction -> postRepository.incrementLikeCount(postReaction.getPostIdx()));
        }

        @Test
        @DisplayName("정상적으로 증가한다")
        void incrementLikeCount() {
            // when
            List<Community> actualCommunities = communityRepository.findAllCommunity(new Condition());
            Map<Long, Integer> actualLikeCounts = actualCommunities.stream()
                    .collect(Collectors.toMap(
                            Community::getPostIdx, Community::getLikeCount));
            Map<Long, Integer> expectedLikeCounts = postLikeReactions.stream()
                    .collect(Collectors.groupingBy(PostReaction::getPostIdx, Collectors.collectingAndThen(
                            Collectors.counting(), Long::intValue
                    )));

            // then
            assertThat(actualLikeCounts).isEqualTo(expectedLikeCounts);
        }

        @Test
        @DisplayName("상태값이 변경된다")
        void updateReactionStatus() {
            // when
            boolean hasAnyLikeReactions = postLikeReactions.stream()
                    .allMatch(postReaction -> postRepository.getPostReaction(postReaction).getReaction() == Reaction.LIKE);

            // then
            assertThat(hasAnyLikeReactions).isTrue();
        }
    }

    @Nested
    @DisplayName("게시글 좋아요의 감소")
    class DecrementLikeCount {

        public List<Member> members;
        public List<Community> communities;
        public List<PostReaction> postLikeReactions;
        public List<PostReaction> postUnlikeReactions;

        @BeforeEach
        void setUp() {
            // given
            members = Arrays.asList(
                    createMember(MEMBER1_ID, MEMBER1_NAME, MEMBER1_NICKNAME),
                    createMember(MEMBER2_ID, MEMBER2_NAME, MEMBER2_NICKNAME),
                    createMember(MEMBER3_ID, MEMBER3_NAME, MEMBER3_NICKNAME),
                    createMember(MEMBER4_ID, MEMBER4_NAME, MEMBER4_NICKNAME),
                    createMember(MEMBER5_ID, MEMBER5_NAME, MEMBER5_NICKNAME));
            communities = members.stream()
                    .map(member -> createCommunityForSave(member.getId())).collect(Collectors.toList());
            postLikeReactions = Arrays.asList(
                    createPostReaction(1L, 1L, Reaction.LIKE),
                    createPostReaction(1L, 2L, Reaction.LIKE),
                    createPostReaction(1L, 3L, Reaction.LIKE),

                    createPostReaction(2L, 2L, Reaction.LIKE),
                    createPostReaction(2L, 1L, Reaction.LIKE),

                    createPostReaction(3L, 3L, Reaction.LIKE),

                    createPostReaction(4L, 4L, Reaction.LIKE),
                    createPostReaction(4L, 1L, Reaction.LIKE),

                    createPostReaction(5L, 5L, Reaction.LIKE)
            );
            postUnlikeReactions = Arrays.asList(
                    createPostReaction(1L, 1L, Reaction.UNLIKE),
                    createPostReaction(1L, 2L, Reaction.UNLIKE),
                    createPostReaction(1L, 3L, Reaction.UNLIKE),

                    createPostReaction(2L, 2L, Reaction.UNLIKE),
                    createPostReaction(2L, 1L, Reaction.UNLIKE),

                    createPostReaction(3L, 3L, Reaction.UNLIKE),

                    createPostReaction(4L, 4L, Reaction.UNLIKE),
                    createPostReaction(4L, 1L, Reaction.UNLIKE),

                    createPostReaction(5L, 5L, Reaction.UNLIKE)
            );

            members.forEach(member -> memberRepository.saveMember(member));
            communities.forEach(community -> communityRepository.saveCommunity(community));
            postLikeReactions.forEach(postReaction -> postRepository.savePostReaction(postReaction));
            postLikeReactions.forEach(postReaction -> postRepository.incrementLikeCount(postReaction.getPostIdx()));
        }

        @Test
        @DisplayName("정상적으로 감소한다")
        void decrementLikeCount() {
            // when
            List<Community> actualCommunitiesBeforeUpdate = communityRepository.findAllCommunity(new Condition());
            Map<Long, Integer> actualLikeCountsBeforeUpdate = actualCommunitiesBeforeUpdate.stream()
                    .collect(Collectors.toMap(
                            Community::getPostIdx, Community::getLikeCount));
            Map<Long, Integer> expectedLikeCountsBeforeUpdate = postLikeReactions.stream()
                    .collect(Collectors.groupingBy(PostReaction::getPostIdx, Collectors.collectingAndThen(
                            Collectors.counting(), Long::intValue
                    )));
            boolean allCommunitiesHaveNonZeroLikeCountsBeforeUpdate = actualCommunitiesBeforeUpdate.stream()
                    .allMatch(community -> community.getLikeCount() != 0);

            postUnlikeReactions.forEach(postReaction -> postRepository.updatePostReaction(postReaction));
            postUnlikeReactions.forEach(postReaction -> postRepository.decrementLikeCount(postReaction.getPostIdx()));

            List<Community> actualCommunitiesAfterUpdate = communityRepository.findAllCommunity(new Condition());
            boolean allCommunitiesHaveZeroLikeCountsAfterUpdate = actualCommunitiesAfterUpdate.stream()
                    .allMatch(community -> community.getLikeCount() == 0);

            // then
            assertThat(allCommunitiesHaveNonZeroLikeCountsBeforeUpdate).isTrue();
            assertThat(actualLikeCountsBeforeUpdate).isEqualTo(expectedLikeCountsBeforeUpdate);
            assertThat(allCommunitiesHaveZeroLikeCountsAfterUpdate).isTrue();
        }

        @Test
        @DisplayName("상태값이 변경된다")
        void updateReactionStatus() {
            // when
            boolean hasAnyLikeReactionsBeforeUpdate = postLikeReactions.stream()
                    .allMatch(postReaction -> postRepository.getPostReaction(postReaction).getReaction() == Reaction.LIKE);

            postUnlikeReactions.forEach(postReaction -> postRepository.updatePostReaction(postReaction));
            postUnlikeReactions.forEach(postReaction -> postRepository.decrementLikeCount(postReaction.getPostIdx()));

            boolean hasAnyUnlikeReactionsAfterUpdate = postLikeReactions.stream()
                    .allMatch(postReaction -> postRepository.getPostReaction(postReaction).getReaction() == Reaction.UNLIKE);

            // then
            assertThat(hasAnyLikeReactionsBeforeUpdate).isTrue();
            assertThat(hasAnyUnlikeReactionsAfterUpdate).isTrue();
        }
    }

    @Nested
    @DisplayName("게시글 싫어요의 증가")
    class incrementDislikeCount {

        public List<Member> members;
        public List<Community> communities;
        public List<PostReaction> postDisLikeReactions;

        @BeforeEach
        void setUp() {
            // given
            members = Arrays.asList(
                    createMember(MEMBER1_ID, MEMBER1_NAME, MEMBER1_NICKNAME),
                    createMember(MEMBER2_ID, MEMBER2_NAME, MEMBER2_NICKNAME),
                    createMember(MEMBER3_ID, MEMBER3_NAME, MEMBER3_NICKNAME),
                    createMember(MEMBER4_ID, MEMBER4_NAME, MEMBER4_NICKNAME),
                    createMember(MEMBER5_ID, MEMBER5_NAME, MEMBER5_NICKNAME));
            communities = members.stream()
                    .map(member -> createCommunityForSave(member.getId())).collect(Collectors.toList());
            postDisLikeReactions = Arrays.asList(
                    createPostReaction(1L, 1L, Reaction.DISLIKE),
                    createPostReaction(1L, 2L, Reaction.DISLIKE),
                    createPostReaction(1L, 3L, Reaction.DISLIKE),

                    createPostReaction(2L, 2L, Reaction.DISLIKE),
                    createPostReaction(2L, 1L, Reaction.DISLIKE),

                    createPostReaction(3L, 3L, Reaction.DISLIKE),

                    createPostReaction(4L, 4L, Reaction.DISLIKE),
                    createPostReaction(4L, 1L, Reaction.DISLIKE),

                    createPostReaction(5L, 5L, Reaction.DISLIKE)
            );

            members.forEach(member -> memberRepository.saveMember(member));
            communities.forEach(community -> communityRepository.saveCommunity(community));
            postDisLikeReactions.forEach(postReaction -> postRepository.savePostReaction(postReaction));
            postDisLikeReactions.forEach(postReaction -> postRepository.incrementDislikeCount(postReaction.getPostIdx()));
        }

        @Test
        @DisplayName("정상적으로 증가한다")
        void incrementDislikeCount() {
            // when
            List<Community> actualCommunities = communityRepository.findAllCommunity(new Condition());
            Map<Long, Integer> actualDisLikeCounts = actualCommunities.stream()
                    .collect(Collectors.toMap(
                            Community::getPostIdx, Community::getDislikeCount));
            Map<Long, Integer> expectedDisLikeCounts = postDisLikeReactions.stream()
                    .collect(Collectors.groupingBy(PostReaction::getPostIdx, Collectors.collectingAndThen(
                            Collectors.counting(), Long::intValue
                    )));

            // then
            assertThat(actualDisLikeCounts).isEqualTo(expectedDisLikeCounts);
        }

        @Test
        @DisplayName("상태값이 변경된다")
        void updateReactionStatus() {
            // when
            boolean hasAnyDislikeReactions = postDisLikeReactions.stream()
                    .allMatch(postReaction -> postRepository.getPostReaction(postReaction).getReaction() == Reaction.DISLIKE);

            // then
            assertThat(hasAnyDislikeReactions).isTrue();
        }
    }

    @Nested
    @DisplayName("게시글 싫어요의 감소")
    class DecrementDislikeCount {

        public List<Member> members;
        public List<Community> communities;
        public List<PostReaction> postDisLikeReactions;
        public List<PostReaction> postUnDislikeReactions;

        @BeforeEach
        void setUp() {
            // given
            members = Arrays.asList(
                    createMember(MEMBER1_ID, MEMBER1_NAME, MEMBER1_NICKNAME),
                    createMember(MEMBER2_ID, MEMBER2_NAME, MEMBER2_NICKNAME),
                    createMember(MEMBER3_ID, MEMBER3_NAME, MEMBER3_NICKNAME),
                    createMember(MEMBER4_ID, MEMBER4_NAME, MEMBER4_NICKNAME),
                    createMember(MEMBER5_ID, MEMBER5_NAME, MEMBER5_NICKNAME));
            communities = members.stream()
                    .map(member -> createCommunityForSave(member.getId())).collect(Collectors.toList());
            postDisLikeReactions = Arrays.asList(
                    createPostReaction(1L, 1L, Reaction.DISLIKE),
                    createPostReaction(1L, 2L, Reaction.DISLIKE),
                    createPostReaction(1L, 3L, Reaction.DISLIKE),

                    createPostReaction(2L, 2L, Reaction.DISLIKE),
                    createPostReaction(2L, 1L, Reaction.DISLIKE),

                    createPostReaction(3L, 3L, Reaction.DISLIKE),

                    createPostReaction(4L, 4L, Reaction.DISLIKE),
                    createPostReaction(4L, 1L, Reaction.DISLIKE),

                    createPostReaction(5L, 5L, Reaction.DISLIKE)
            );
            postUnDislikeReactions = Arrays.asList(
                    createPostReaction(1L, 1L, Reaction.UNDISLIKE),
                    createPostReaction(1L, 2L, Reaction.UNDISLIKE),
                    createPostReaction(1L, 3L, Reaction.UNDISLIKE),

                    createPostReaction(2L, 2L, Reaction.UNDISLIKE),
                    createPostReaction(2L, 1L, Reaction.UNDISLIKE),

                    createPostReaction(3L, 3L, Reaction.UNDISLIKE),

                    createPostReaction(4L, 4L, Reaction.UNDISLIKE),
                    createPostReaction(4L, 1L, Reaction.UNDISLIKE),

                    createPostReaction(5L, 5L, Reaction.UNDISLIKE)
            );

            members.forEach(member -> memberRepository.saveMember(member));
            communities.forEach(community -> communityRepository.saveCommunity(community));
            postDisLikeReactions.forEach(postReaction -> postRepository.savePostReaction(postReaction));
            postDisLikeReactions.forEach(postReaction -> postRepository.incrementDislikeCount(postReaction.getPostIdx()));
        }

        @Test
        @DisplayName("정상적으로 감소한다")
        void decrementDislikeCount() {
            // when
            List<Community> actualCommunitiesBeforeUpdate = communityRepository.findAllCommunity(new Condition());
            Map<Long, Integer> actualDislikeCountsBeforeUpdate = actualCommunitiesBeforeUpdate.stream()
                    .collect(Collectors.toMap(
                            Community::getPostIdx, Community::getDislikeCount));
            Map<Long, Integer> expectedDislikeCountsBeforeUpdate = postDisLikeReactions.stream()
                    .collect(Collectors.groupingBy(PostReaction::getPostIdx, Collectors.collectingAndThen(
                            Collectors.counting(), Long::intValue
                    )));
            boolean allCommunitiesHaveNonZeroDislikeCountsBeforeUpdate = actualCommunitiesBeforeUpdate.stream()
                    .allMatch(community -> community.getDislikeCount() != 0);

            postUnDislikeReactions.forEach(postReaction -> postRepository.updatePostReaction(postReaction));
            postUnDislikeReactions.forEach(postReaction -> postRepository.decrementDislikeCount(postReaction.getPostIdx()));

            List<Community> actualCommunitiesAfterUpdate = communityRepository.findAllCommunity(new Condition());
            boolean allCommunitiesHaveZeroDislikeCountsBeforeUpdate = actualCommunitiesAfterUpdate.stream()
                    .allMatch(community -> community.getDislikeCount() == 0);

            // then
            assertThat(allCommunitiesHaveNonZeroDislikeCountsBeforeUpdate).isTrue();
            assertThat(actualDislikeCountsBeforeUpdate).isEqualTo(expectedDislikeCountsBeforeUpdate);
            assertThat(allCommunitiesHaveZeroDislikeCountsBeforeUpdate).isTrue();
        }

        @Test
        @DisplayName("상태값이 변경된다")
        void updateReactionStatus() {
            // when
            boolean hasAnyDislikeReactionsBeforeUpdate = postDisLikeReactions.stream()
                    .allMatch(postReaction -> postRepository.getPostReaction(postReaction).getReaction() == Reaction.DISLIKE);

            postUnDislikeReactions.forEach(postReaction -> postRepository.updatePostReaction(postReaction));
            postUnDislikeReactions.forEach(postReaction -> postRepository.decrementLikeCount(postReaction.getPostIdx()));

            boolean hasAnyUnDislikeReactionsAfterUpdate = postDisLikeReactions.stream()
                    .allMatch(postReaction -> postRepository.getPostReaction(postReaction).getReaction() == Reaction.UNDISLIKE);

            // then
            assertThat(hasAnyDislikeReactionsBeforeUpdate).isTrue();
            assertThat(hasAnyUnDislikeReactionsAfterUpdate).isTrue();
        }
    }
}