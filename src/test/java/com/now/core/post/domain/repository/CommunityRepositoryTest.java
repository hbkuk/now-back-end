package com.now.core.post.domain.repository;

import com.now.config.annotations.RepositoryTest;
import com.now.core.attachment.domain.AttachmentRepository;
import com.now.core.category.domain.constants.Category;
import com.now.core.comment.domain.CommentRepository;
import com.now.core.member.domain.Member;
import com.now.core.member.domain.MemberRepository;
import com.now.core.post.domain.Community;
import com.now.core.post.presentation.dto.Condition;
import com.now.core.post.presentation.dto.PostReaction;
import com.now.core.post.presentation.dto.constants.Reaction;
import com.now.core.post.presentation.dto.constants.Sort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static com.now.config.fixtures.member.MemberFixture.*;
import static com.now.config.fixtures.post.CommunityFixture.createCommunityForSave;
import static com.now.config.fixtures.post.CommunityFixture.createCommunityForUpdate;
import static com.now.config.fixtures.post.dto.ConditionFixture.createCondition;
import static com.now.config.fixtures.post.dto.ConditionFixture.createConditionOnlySort;
import static com.now.config.fixtures.post.dto.PostReactionFixture.createPostReaction;
import static com.now.config.utilities.SortUtils.isChronologicalOrder;
import static com.now.config.utilities.SortUtils.isFirstElementMaxAndDescending;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RepositoryTest
@DisplayName("커뮤니티 레포지토리")
class CommunityRepositoryTest {

    @Autowired
    protected PostRepository postRepository;

    @Autowired
    protected CommunityRepository communityRepository;

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

    @Nested
    @DisplayName("모든 커뮤니티 게시글을 찾을 때")
    class FindAll {

        @Nested
        @DisplayName("정렬 조건")
        class Sort_of {

            @Test
            @DisplayName("최신순으로 정렬 후 조회된다")
            void filter_only_sort_latest() {
                // given
                List<Member> members = Arrays.asList(
                        createMember(MEMBER1_ID, MEMBER1_NAME, MEMBER1_NICKNAME),
                        createMember(MEMBER2_ID, MEMBER2_NAME, MEMBER2_NICKNAME),
                        createMember(MEMBER3_ID, MEMBER3_NAME, MEMBER3_NICKNAME),
                        createMember(MEMBER4_ID, MEMBER4_NAME, MEMBER4_NICKNAME),
                        createMember(MEMBER5_ID, MEMBER5_NAME, MEMBER5_NICKNAME));

                List<Community> expectedCommunities = members.stream()
                        .map(member -> createCommunityForSave(member.getId())).collect(Collectors.toList());

                Condition condition = createConditionOnlySort(Sort.LATEST);

                // when
                members.forEach(member -> memberRepository.saveMember(member));
                expectedCommunities.forEach(community -> communityRepository.saveCommunity(community));
                List<Community> actualCommunities = communityRepository.findAllCommunity(condition.updatePage());

                List<LocalDateTime> regDateTimes = actualCommunities.stream()
                        .map(Community::getRegDate)
                        .collect(Collectors.toList());

                // then
                assertThat(actualCommunities).hasSize(expectedCommunities.size());
                assertTrue(isChronologicalOrder(regDateTimes));
            }

            @Test
            @DisplayName("추천순(좋아요-싫어요)으로 정렬 후 조회된다")
            void filter_only_sort_recommended() {
                // given
                List<Member> members = Arrays.asList(
                        createMember(MEMBER1_ID, MEMBER1_NAME, MEMBER1_NICKNAME),
                        createMember(MEMBER2_ID, MEMBER2_NAME, MEMBER2_NICKNAME),
                        createMember(MEMBER3_ID, MEMBER3_NAME, MEMBER3_NICKNAME),
                        createMember(MEMBER4_ID, MEMBER4_NAME, MEMBER4_NICKNAME),
                        createMember(MEMBER5_ID, MEMBER5_NAME, MEMBER5_NICKNAME));

                List<Community> expectedCommunities = members.stream()
                        .map(member -> createCommunityForSave(member.getId())).collect(Collectors.toList());

                Condition condition = createConditionOnlySort(Sort.RECOMMENDED);

                List<PostReaction> postLikeReactions = Arrays.asList(
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

                List<PostReaction> postUnlikeReactions = Arrays.asList(
                        createPostReaction(1L, 2L, Reaction.UNLIKE),
                        createPostReaction(1L, 3L, Reaction.UNLIKE),

                        createPostReaction(2L, 2L, Reaction.UNLIKE),
                        createPostReaction(2L, 1L, Reaction.UNLIKE)
                );

                // when
                members.forEach(member -> memberRepository.saveMember(member));
                expectedCommunities.forEach(community -> communityRepository.saveCommunity(community));
                postLikeReactions.forEach(postReaction -> postRepository.savePostReaction(postReaction));
                postLikeReactions.forEach(postReaction -> postRepository.incrementLikeCount(postReaction.getPostIdx()));

                postUnlikeReactions.forEach(postReaction -> postRepository.updatePostReaction(postReaction));
                postUnlikeReactions.forEach(postReaction -> postRepository.decrementLikeCount(postReaction.getPostIdx()));

                List<Community> actualCommunities = communityRepository.findAllCommunity(condition.updatePage());

                List<Integer> likeMinusDislikeCounts = actualCommunities.stream()
                        .map(community -> community.getLikeCount() - community.getDislikeCount())
                        .collect(Collectors.toList());

                // then
                assertThat(actualCommunities).hasSize(expectedCommunities.size());
                assertTrue(isFirstElementMaxAndDescending(likeMinusDislikeCounts));
            }

            @Test
            @DisplayName("조회수순으로 정렬 후 조회된다")
            void filter_only_sort_most_viewed() {
                // given
                List<Member> members = Arrays.asList(
                        createMember(MEMBER1_ID, MEMBER1_NAME, MEMBER1_NICKNAME),
                        createMember(MEMBER2_ID, MEMBER2_NAME, MEMBER2_NICKNAME),
                        createMember(MEMBER3_ID, MEMBER3_NAME, MEMBER3_NICKNAME),
                        createMember(MEMBER4_ID, MEMBER4_NAME, MEMBER4_NICKNAME),
                        createMember(MEMBER5_ID, MEMBER5_NAME, MEMBER5_NICKNAME));

                List<Community> expectedCommunities = members.stream()
                        .map(member -> createCommunityForSave(member.getId())).collect(Collectors.toList());

                Condition condition = createConditionOnlySort(Sort.MOST_VIEWED);

                // when
                members.forEach(member -> memberRepository.saveMember(member));
                expectedCommunities.forEach(community -> communityRepository.saveCommunity(community));

                List<Long> expectedPostIndexes = expectedCommunities.stream()
                        .map(Community::getPostIdx)
                        .collect(Collectors.toList());
                randomIncreaseViewCount(postRepository, expectedPostIndexes, new int[]{2, 3, 1, 2, 1});

                List<Community> actualCommunities = communityRepository.findAllCommunity(condition.updatePage());

                List<Integer> viewCounts = actualCommunities.stream()
                        .map(Community::getViewCount)
                        .collect(Collectors.toList());

                // then
                assertThat(actualCommunities).hasSize(expectedCommunities.size());
                assertTrue(isFirstElementMaxAndDescending(viewCounts));
            }
        }

        @Nested
        @DisplayName("카테고리")
        class Category_of {

            @Test
            @DisplayName("특정 카테고리의 설정된 게시글만 조회된다")
            void filter_only_category() {
                // given
                List<Member> members = Arrays.asList(
                        createMember(MEMBER1_ID, MEMBER1_NAME, MEMBER1_NICKNAME),
                        createMember(MEMBER2_ID, MEMBER2_NAME, MEMBER2_NICKNAME),
                        createMember(MEMBER3_ID, MEMBER3_NAME, MEMBER3_NICKNAME),
                        createMember(MEMBER4_ID, MEMBER4_NAME, MEMBER4_NICKNAME),
                        createMember(MEMBER5_ID, MEMBER5_NAME, MEMBER5_NICKNAME));

                List<Community> expectedCommunities = Arrays.asList(
                        createCommunityForSave(members.get(0).getId(), Category.COMMUNITY_STUDY),
                        createCommunityForSave(members.get(1).getId(), Category.LIFESTYLE),
                        createCommunityForSave(members.get(2).getId(), Category.COMMUNITY_STUDY),
                        createCommunityForSave(members.get(3).getId(), Category.LIFESTYLE),
                        createCommunityForSave(members.get(4).getId(), Category.COMMUNITY_STUDY)
                );

                Condition condition = createCondition(Sort.LATEST, Category.LIFESTYLE);

                // when
                members.forEach(member -> memberRepository.saveMember(member));
                expectedCommunities.forEach(community -> communityRepository.saveCommunity(community));
                List<Community> actualCommunities = communityRepository.findAllCommunity(condition.updatePage());

                int expectedSize = (int) expectedCommunities.stream()
                        .filter(community ->
                                community.getCategory() == condition.getCategory()).count();

                List<LocalDateTime> regDateTimes = actualCommunities.stream()
                        .map(Community::getRegDate)
                        .collect(Collectors.toList());

                // then
                assertThat(actualCommunities).hasSize(expectedSize);
                assertTrue(isChronologicalOrder(regDateTimes));
            }

        }

        @Nested
        @DisplayName("키워드")
        class Keyword_of {

            @Test
            @DisplayName("특정 키워드가 제목, 닉네임, 내용 중 하나 혹은 여러 항목에 포함된 게시글만 조회된다")
            void filter_only_keyword_match_content() {
                // given
                List<Member> members = Arrays.asList(
                        createMember(MEMBER1_ID, MEMBER1_NAME, "애플"),
                        createMember(MEMBER2_ID, MEMBER2_NAME, "apple"),
                        createMember(MEMBER3_ID, MEMBER3_NAME, "바나나"),
                        createMember(MEMBER4_ID, MEMBER4_NAME, "banana"),
                        createMember(MEMBER5_ID, MEMBER5_NAME, "멜론"));

                List<Community> communities = Arrays.asList(
                        createCommunityForSave(members.get(0).getId(), 1L, members.get(0).getNickname(), Category.COMMUNITY_STUDY, "사랑", "기쁨"),
                        createCommunityForSave(members.get(1).getId(), 2L, members.get(1).getNickname(), Category.LIFESTYLE, "love", "안녕"),
                        createCommunityForSave(members.get(2).getId(), 3L, members.get(2).getNickname(), Category.COMMUNITY_STUDY, "탄수화물", "식이섬유"),
                        createCommunityForSave(members.get(3).getId(), 4L, members.get(3).getNickname(), Category.LIFESTYLE, "단백질", "운동"),
                        createCommunityForSave(members.get(4).getId(), 5L, members.get(4).getNickname(), Category.COMMUNITY_STUDY, "영업소", "구입처")
                );

                Condition condition = createCondition(Sort.LATEST, null, "구입처");

                // when
                members.forEach(member -> memberRepository.saveMember(member));
                communities.forEach(community -> communityRepository.saveCommunity(community));
                List<Community> actualCommunities = communityRepository.findAllCommunity(condition.updatePage());

                // 필터링 조건을 만족하는 커뮤니티만 선택
                Predicate<Community> keywordFilter = community ->
                        community.getTitle().contains(condition.getKeyword()) ||
                                community.getMemberNickname().contains(condition.getKeyword()) ||
                                community.getContent().contains(condition.getKeyword());

                int expectedSize = (int) communities.stream()
                        .filter(keywordFilter)
                        .count();

                // then
                assertThat(actualCommunities).hasSize(expectedSize);
            }

            @Test
            @DisplayName("특정 키워드(제목, 닉네임, 내용)가 포함된 게시글만 조회된다")
            void filter_only_keyword_match_title() {
                // given
                List<Member> members = Arrays.asList(
                        createMember(MEMBER1_ID, MEMBER1_NAME, "애플"),
                        createMember(MEMBER2_ID, MEMBER2_NAME, "apple"),
                        createMember(MEMBER3_ID, MEMBER3_NAME, "바나나"),
                        createMember(MEMBER4_ID, MEMBER4_NAME, "banana"),
                        createMember(MEMBER5_ID, MEMBER5_NAME, "멜론"));

                List<Community> communities = Arrays.asList(
                        createCommunityForSave(members.get(0).getId(), 1L, members.get(0).getNickname(), Category.COMMUNITY_STUDY, "사랑", "기쁨"),
                        createCommunityForSave(members.get(1).getId(), 2L, members.get(1).getNickname(), Category.LIFESTYLE, "love", "안녕"),
                        createCommunityForSave(members.get(2).getId(), 3L, members.get(2).getNickname(), Category.COMMUNITY_STUDY, "탄수화물", "식이섬유"),
                        createCommunityForSave(members.get(3).getId(), 4L, members.get(3).getNickname(), Category.LIFESTYLE, "단백질", "운동"),
                        createCommunityForSave(members.get(4).getId(), 5L, members.get(4).getNickname(), Category.COMMUNITY_STUDY, "영업소", "구입처")
                );

                Condition condition = createCondition(Sort.LATEST, null, "lo");

                // when
                members.forEach(member -> memberRepository.saveMember(member));
                communities.forEach(community -> communityRepository.saveCommunity(community));
                List<Community> actualCommunities = communityRepository.findAllCommunity(condition.updatePage());

                // 필터링 조건을 만족하는 커뮤니티만 선택
                Predicate<Community> keywordFilter = community ->
                        community.getTitle().contains(condition.getKeyword()) ||
                                community.getMemberNickname().contains(condition.getKeyword()) ||
                                community.getContent().contains(condition.getKeyword());

                int expectedSize = (int) communities.stream()
                        .filter(keywordFilter)
                        .count();

                // then
                assertThat(actualCommunities).hasSize(expectedSize);
            }

            @Test
            @DisplayName("특정 키워드(제목, 닉네임, 내용)가 포함된 게시글만 조회된다")
            void filter_only_keyword_match_memberNickname() {
                // given
                List<Member> members = Arrays.asList(
                        createMember(MEMBER1_ID, MEMBER1_NAME, "애플"),
                        createMember(MEMBER2_ID, MEMBER2_NAME, "apple"),
                        createMember(MEMBER3_ID, MEMBER3_NAME, "바나나"),
                        createMember(MEMBER4_ID, MEMBER4_NAME, "banana"),
                        createMember(MEMBER5_ID, MEMBER5_NAME, "멜론"));

                List<Community> communities = Arrays.asList(
                        createCommunityForSave(members.get(0).getId(), 1L, members.get(0).getNickname(), Category.COMMUNITY_STUDY, "사랑", "기쁨"),
                        createCommunityForSave(members.get(1).getId(), 2L, members.get(1).getNickname(), Category.LIFESTYLE, "love", "안녕"),
                        createCommunityForSave(members.get(2).getId(), 3L, members.get(2).getNickname(), Category.COMMUNITY_STUDY, "탄수화물", "식이섬유"),
                        createCommunityForSave(members.get(3).getId(), 4L, members.get(3).getNickname(), Category.LIFESTYLE, "단백질", "운동"),
                        createCommunityForSave(members.get(4).getId(), 5L, members.get(4).getNickname(), Category.COMMUNITY_STUDY, "영업소", "구입처")
                );

                Condition condition = createCondition(Sort.LATEST, null, "애플");

                // when
                members.forEach(member -> memberRepository.saveMember(member));
                communities.forEach(community -> communityRepository.saveCommunity(community));
                List<Community> actualCommunities = communityRepository.findAllCommunity(condition.updatePage());

                // 필터링 조건을 만족하는 커뮤니티만 선택
                Predicate<Community> keywordFilter = community ->
                        community.getTitle().contains(condition.getKeyword()) ||
                                community.getMemberNickname().contains(condition.getKeyword()) ||
                                community.getContent().contains(condition.getKeyword());

                int expectedSize = (int) communities.stream()
                        .filter(keywordFilter)
                        .count();

                // then
                assertThat(actualCommunities).hasSize(expectedSize);
            }
        }

        @Nested
        @DisplayName("단건 게시글을 찾을 때")
        class FindOne {

            @Test
            @DisplayName("게시글 번호로 조회된다")
            void by_postIdx() {
                // given
                List<Member> members = Arrays.asList(
                        createMember(MEMBER1_ID, MEMBER1_NAME, MEMBER1_NICKNAME),
                        createMember(MEMBER2_ID, MEMBER2_NAME, MEMBER2_NICKNAME),
                        createMember(MEMBER3_ID, MEMBER3_NAME, MEMBER3_NICKNAME),
                        createMember(MEMBER4_ID, MEMBER4_NAME, MEMBER4_NICKNAME),
                        createMember(MEMBER5_ID, MEMBER5_NAME, MEMBER5_NICKNAME));

                List<Community> expectedCommunities = LongStream.range(1L, members.size() + 1L)
                        .mapToObj(index -> createCommunityForSave(index, members.get((int) (index - 1)).getId()))
                        .collect(Collectors.toList());


                // when
                members.forEach(member -> memberRepository.saveMember(member));
                expectedCommunities.forEach(community -> communityRepository.saveCommunity(community));


                // then
                assertThat(communityRepository.findCommunity(1L).getMemberNickname()).isEqualTo(MEMBER1_NICKNAME);
                assertThat(communityRepository.findCommunity(2L).getMemberNickname()).isEqualTo(MEMBER2_NICKNAME);
                assertThat(communityRepository.findCommunity(3L).getMemberNickname()).isEqualTo(MEMBER3_NICKNAME);
                assertThat(communityRepository.findCommunity(4L).getMemberNickname()).isEqualTo(MEMBER4_NICKNAME);
                assertThat(communityRepository.findCommunity(5L).getMemberNickname()).isEqualTo(MEMBER5_NICKNAME);

            }
        }

        @Nested
        @DisplayName("게시글을 수정할 때")
        class Update {

            @Test
            @DisplayName("게시글이 수정된다")
            void updateCommunity() {
                // given
                Long postIdx = 1L;
                Member member = createMember(MEMBER1_ID, MEMBER1_NAME, MEMBER1_NICKNAME);

                Community community = createCommunityForSave(
                        member.getId(), 1L, member.getNickname(), Category.COMMUNITY_STUDY, "사랑", "기쁨");

                Community expectedUpdatedCommunity = createCommunityForUpdate(
                        postIdx, 1L, Category.LIFESTYLE, "슬픔", "눈물");

                // when
                memberRepository.saveMember(member);
                communityRepository.saveCommunity(community);
                communityRepository.updateCommunity(expectedUpdatedCommunity);

                Community actualCommunity = communityRepository.findCommunity(postIdx);

                // then
                assertThat(actualCommunity.getMemberNickname()).isEqualTo(member.getNickname());
                assertThat(actualCommunity.getCategory()).isEqualTo(expectedUpdatedCommunity.getCategory());
                assertThat(actualCommunity.getTitle()).isEqualTo(expectedUpdatedCommunity.getTitle());
                assertThat(actualCommunity.getContent()).isEqualTo(expectedUpdatedCommunity.getContent());
            }
        }

        @Nested
        @DisplayName("게시글을 삭제할 때")
        class Delete {

            @Test
            @DisplayName("게시글이 삭제된다")
            void deleteCommunity() {
                // given
                Long postIdx = 1L;
                Member member = createMember(MEMBER1_ID, MEMBER1_NAME, MEMBER1_NICKNAME);

                Community community = createCommunityForSave(
                        member.getId(), 1L, member.getNickname(), Category.COMMUNITY_STUDY, "사랑", "기쁨");

                // when
                memberRepository.saveMember(member);
                communityRepository.saveCommunity(community);
                communityRepository.deleteCommunity(postIdx);
                Community actualCommunity = communityRepository.findCommunity(postIdx);
                List<Community> actualCommunities = communityRepository.findAllCommunity(createConditionOnlySort(Sort.LATEST).updatePage());

                // then
                assertThat(actualCommunity).isNull();
                assertThat(actualCommunities).isEmpty();
            }
        }
    }
}
