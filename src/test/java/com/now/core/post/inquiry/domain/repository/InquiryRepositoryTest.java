package com.now.core.post.inquiry.domain.repository;

import com.now.config.annotations.RepositoryTest;
import com.now.core.admin.manager.domain.Manager;
import com.now.core.admin.manager.domain.ManagerRepository;
import com.now.core.category.domain.constants.Category;
import com.now.core.comment.domain.CommentRepository;
import com.now.core.member.domain.Member;
import com.now.core.member.domain.MemberRepository;
import com.now.core.post.common.domain.repository.PostRepository;
import com.now.core.post.inquiry.domain.Inquiry;
import com.now.core.post.inquiry.domain.constants.InquiryStatus;
import com.now.core.post.inquiry.domain.repository.InquiryRepository;
import com.now.core.post.common.presentation.dto.Condition;
import com.now.core.post.common.presentation.dto.PostReaction;
import com.now.core.post.common.presentation.dto.constants.Reaction;
import com.now.core.post.common.presentation.dto.constants.Sort;
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

import static com.now.config.fixtures.manager.ManagerFixture.*;
import static com.now.config.fixtures.member.MemberFixture.*;
import static com.now.config.fixtures.post.InquiryFixture.createInquiryForSave;
import static com.now.config.fixtures.post.dto.AnswerFixture.*;
import static com.now.config.fixtures.post.dto.ConditionFixture.createCondition;
import static com.now.config.fixtures.post.dto.ConditionFixture.createConditionOnlySort;
import static com.now.config.fixtures.post.dto.PostReactionFixture.createPostReaction;
import static com.now.config.utilities.SortUtils.isChronologicalOrder;
import static com.now.config.utilities.SortUtils.isFirstElementMaxAndDescending;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RepositoryTest
@DisplayName("문의 레포지토리")
class InquiryRepositoryTest {

    @Autowired
    protected PostRepository postRepository;

    @Autowired
    protected InquiryRepository inquiryRepository;

    @Autowired
    protected MemberRepository memberRepository;

    @Autowired
    protected ManagerRepository managerRepository;

    @Autowired
    protected CommentRepository commentRepository;

    private void randomIncreaseViewCount(PostRepository postRepository, List<Long> postIndexes, int[] viewCounts) {
        for (int i = 0; i < postIndexes.size(); i++) {
            for (int j = 0; j < viewCounts[i]; j++) {
                postRepository.incrementViewCount(postIndexes.get(i));
            }
        }
    }

    @Nested
    @DisplayName("문의 게시글")
    class Inquiry_Post {

        @Nested
        @DisplayName("모든 문의 게시글을 찾을 때")
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

                    List<Inquiry> expectedInquiries = Arrays.asList(
                            createInquiryForSave(1L, members.get(0).getId(), members.get(0).getNickname(), Category.SERVICE, "사랑", "기쁨", false),
                            createInquiryForSave(2L, members.get(1).getId(), members.get(1).getNickname(), Category.TECHNOLOGY, "love", "안녕", false),
                            createInquiryForSave(3L, members.get(2).getId(), members.get(2).getNickname(), Category.SERVICE, "탄수화물", "식이섬유", false),
                            createInquiryForSave(4L, members.get(3).getId(), members.get(3).getNickname(), Category.TECHNOLOGY, "단백질", "운동", false),
                            createInquiryForSave(5L, members.get(4).getId(), members.get(4).getNickname(), Category.SERVICE, "영업소", "구입처", false)
                    );

                    Condition condition = createConditionOnlySort(Sort.LATEST);

                    // when
                    members.forEach(member -> memberRepository.saveMember(member));

                    expectedInquiries.forEach(inquiry -> {
                        inquiryRepository.savePost(inquiry);
                        inquiryRepository.saveInquirySecretSetting(inquiry);
                    });

                    List<Inquiry> actualInquiries = inquiryRepository.findAllInquiries(condition.updatePage());

                    List<LocalDateTime> actualRegDateTimes = actualInquiries.stream()
                            .map(Inquiry::getRegDate)
                            .collect(Collectors.toList());

                    // then
                    assertThat(actualInquiries).hasSize(expectedInquiries.size());
                    assertTrue(isChronologicalOrder(actualRegDateTimes));
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

                    List<Inquiry> expectedInquiries = Arrays.asList(
                            createInquiryForSave(1L, members.get(0).getId(), members.get(0).getNickname(), Category.SERVICE, "사랑", "기쁨", false),
                            createInquiryForSave(2L, members.get(1).getId(), members.get(1).getNickname(), Category.TECHNOLOGY, "love", "안녕", false),
                            createInquiryForSave(3L, members.get(2).getId(), members.get(2).getNickname(), Category.SERVICE, "탄수화물", "식이섬유", false),
                            createInquiryForSave(4L, members.get(3).getId(), members.get(3).getNickname(), Category.TECHNOLOGY, "단백질", "운동", false),
                            createInquiryForSave(5L, members.get(4).getId(), members.get(4).getNickname(), Category.SERVICE, "영업소", "구입처", false)
                    );

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

                    expectedInquiries.forEach(inquiry -> {
                        inquiryRepository.savePost(inquiry);
                        inquiryRepository.saveInquirySecretSetting(inquiry);
                    });
                    postLikeReactions.forEach(postReaction -> postRepository.savePostReaction(postReaction));
                    postLikeReactions.forEach(postReaction -> postRepository.incrementLikeCount(postReaction.getPostIdx()));

                    postUnlikeReactions.forEach(postReaction -> postRepository.updatePostReaction(postReaction));
                    postUnlikeReactions.forEach(postReaction -> postRepository.decrementLikeCount(postReaction.getPostIdx()));

                    List<Inquiry> actualInquiries = inquiryRepository.findAllInquiries(condition.updatePage());

                    List<Integer> likeMinusDislikeCounts = actualInquiries.stream()
                            .map(inquiry -> inquiry.getLikeCount() - inquiry.getDislikeCount())
                            .collect(Collectors.toList());

                    // then
                    assertThat(actualInquiries).hasSize(expectedInquiries.size());
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

                    List<Inquiry> expectedInquiries = Arrays.asList(
                            createInquiryForSave(1L, members.get(0).getId(), members.get(0).getNickname(), Category.SERVICE, "사랑", "기쁨", false),
                            createInquiryForSave(2L, members.get(1).getId(), members.get(1).getNickname(), Category.TECHNOLOGY, "love", "안녕", false),
                            createInquiryForSave(3L, members.get(2).getId(), members.get(2).getNickname(), Category.SERVICE, "탄수화물", "식이섬유", false),
                            createInquiryForSave(4L, members.get(3).getId(), members.get(3).getNickname(), Category.TECHNOLOGY, "단백질", "운동", false),
                            createInquiryForSave(5L, members.get(4).getId(), members.get(4).getNickname(), Category.SERVICE, "영업소", "구입처", false)
                    );

                    Condition condition = createConditionOnlySort(Sort.MOST_VIEWED);

                    // when
                    members.forEach(member -> memberRepository.saveMember(member));

                    expectedInquiries.forEach(inquiry -> {
                        inquiryRepository.savePost(inquiry);
                        inquiryRepository.saveInquirySecretSetting(inquiry);
                    });

                    List<Long> expectedPostIndexes = expectedInquiries.stream()
                            .map(Inquiry::getPostIdx)
                            .collect(Collectors.toList());
                    randomIncreaseViewCount(postRepository, expectedPostIndexes, new int[]{2, 3, 1, 2, 1});

                    List<Inquiry> actualInquiries = inquiryRepository.findAllInquiries(condition.updatePage());

                    List<Integer> viewCounts = actualInquiries.stream()
                            .map(Inquiry::getViewCount)
                            .collect(Collectors.toList());

                    // then
                    assertThat(actualInquiries).hasSize(expectedInquiries.size());
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

                    List<Inquiry> expectedInquiries = Arrays.asList(
                            createInquiryForSave(1L, members.get(0).getId(), members.get(0).getNickname(), Category.SERVICE, "사랑", "기쁨", false),
                            createInquiryForSave(2L, members.get(1).getId(), members.get(1).getNickname(), Category.TECHNOLOGY, "love", "안녕", false),
                            createInquiryForSave(3L, members.get(2).getId(), members.get(2).getNickname(), Category.SERVICE, "탄수화물", "식이섬유", false),
                            createInquiryForSave(4L, members.get(3).getId(), members.get(3).getNickname(), Category.TECHNOLOGY, "단백질", "운동", false),
                            createInquiryForSave(5L, members.get(4).getId(), members.get(4).getNickname(), Category.SERVICE, "영업소", "구입처", false)
                    );

                    Condition condition = createCondition(Sort.LATEST, Category.SERVICE);

                    // when
                    members.forEach(member -> memberRepository.saveMember(member));

                    expectedInquiries.forEach(inquiry -> {
                        inquiryRepository.savePost(inquiry);
                        inquiryRepository.saveInquirySecretSetting(inquiry);
                    });

                    List<Inquiry> actualInquiries = inquiryRepository.findAllInquiries(condition.updatePage());

                    int expectedSize = (int) expectedInquiries.stream()
                            .filter(inquiry -> inquiry.getCategory() == condition.getCategory()).count();

                    List<LocalDateTime> regDateTimes = actualInquiries.stream()
                            .map(Inquiry::getRegDate)
                            .collect(Collectors.toList());

                    // then
                    assertThat(actualInquiries).hasSize(expectedSize);
                    assertTrue(isChronologicalOrder(regDateTimes));
                }
            }

            @Nested
            @DisplayName("키워드")
            class Keyword_of {

                @Test
                @DisplayName("특정 키워드가 제목, 닉네임, 중 하나 혹은 여러 항목에 포함된 게시글만 조회된다")
                void filter_only_keyword_match_content() {
                    // given
                    List<Member> members = Arrays.asList(
                            createMember(MEMBER1_ID, MEMBER1_NAME, "애플"),
                            createMember(MEMBER2_ID, MEMBER2_NAME, "apple"),
                            createMember(MEMBER3_ID, MEMBER3_NAME, "바나나"),
                            createMember(MEMBER4_ID, MEMBER4_NAME, "banana"),
                            createMember(MEMBER5_ID, MEMBER5_NAME, "멜론"));

                    List<Inquiry> expectedInquiries = Arrays.asList(
                            createInquiryForSave(1L, members.get(0).getId(), members.get(0).getNickname(), Category.SERVICE, "사랑", "기쁨", false),
                            createInquiryForSave(2L, members.get(1).getId(), members.get(1).getNickname(), Category.TECHNOLOGY, "love", "안녕", false),
                            createInquiryForSave(3L, members.get(2).getId(), members.get(2).getNickname(), Category.SERVICE, "탄수화물", "식이섬유", false),
                            createInquiryForSave(4L, members.get(3).getId(), members.get(3).getNickname(), Category.TECHNOLOGY, "단백질", "운동", false),
                            createInquiryForSave(5L, members.get(4).getId(), members.get(4).getNickname(), Category.SERVICE, "영업소", "구입처", false)
                    );

                    Condition condition = createCondition(Sort.LATEST, null, "구입처");

                    // when
                    members.forEach(member -> memberRepository.saveMember(member));

                    expectedInquiries.forEach(inquiry -> {
                        inquiryRepository.savePost(inquiry);
                        inquiryRepository.saveInquirySecretSetting(inquiry);
                    });

                    List<Inquiry> actualInquiries = inquiryRepository.findAllInquiries(condition.updatePage());

                    // 필터링 조건을 만족하는 문의만 선택
                    Predicate<Inquiry> keywordFilter = inquiry ->
                            inquiry.getTitle().contains(condition.getKeyword()) ||
                                    inquiry.getMemberNickname().contains(condition.getKeyword()) ||
                                    inquiry.getContent().contains(condition.getKeyword());

                    int expectedSize = (int) expectedInquiries.stream()
                            .filter(keywordFilter)
                            .count();

                    // then
                    assertThat(actualInquiries).hasSize(expectedSize);
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

                    List<Inquiry> expectedInquiries = Arrays.asList(
                            createInquiryForSave(1L, members.get(0).getId(), members.get(0).getNickname(), Category.SERVICE, "사랑", "기쁨", false),
                            createInquiryForSave(2L, members.get(1).getId(), members.get(1).getNickname(), Category.TECHNOLOGY, "love", "안녕", false),
                            createInquiryForSave(3L, members.get(2).getId(), members.get(2).getNickname(), Category.SERVICE, "탄수화물", "식이섬유", false),
                            createInquiryForSave(4L, members.get(3).getId(), members.get(3).getNickname(), Category.TECHNOLOGY, "단백질", "운동", false),
                            createInquiryForSave(5L, members.get(4).getId(), members.get(4).getNickname(), Category.SERVICE, "영업소", "구입처", false)
                    );
                    Condition condition = createCondition(Sort.LATEST, null, "lo");

                    // when
                    members.forEach(member -> memberRepository.saveMember(member));

                    expectedInquiries.forEach(inquiry -> {
                        inquiryRepository.savePost(inquiry);
                        inquiryRepository.saveInquirySecretSetting(inquiry);
                    });

                    List<Inquiry> actualInquiries = inquiryRepository.findAllInquiries(condition.updatePage());

                    // 필터링 조건을 만족하는 문의만 선택
                    Predicate<Inquiry> keywordFilter = inquiry ->
                            inquiry.getTitle().contains(condition.getKeyword()) ||
                                    inquiry.getMemberNickname().contains(condition.getKeyword()) ||
                                    inquiry.getContent().contains(condition.getKeyword());

                    int expectedSize = (int) expectedInquiries.stream()
                            .filter(keywordFilter)
                            .count();

                    // then
                    assertThat(actualInquiries).hasSize(expectedSize);
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

                    List<Inquiry> expectedInquiries = Arrays.asList(
                            createInquiryForSave(1L, members.get(0).getId(), members.get(0).getNickname(), Category.SERVICE, "사랑", "기쁨", false),
                            createInquiryForSave(2L, members.get(1).getId(), members.get(1).getNickname(), Category.TECHNOLOGY, "love", "안녕", false),
                            createInquiryForSave(3L, members.get(2).getId(), members.get(2).getNickname(), Category.SERVICE, "탄수화물", "식이섬유", false),
                            createInquiryForSave(4L, members.get(3).getId(), members.get(3).getNickname(), Category.TECHNOLOGY, "단백질", "운동", false),
                            createInquiryForSave(5L, members.get(4).getId(), members.get(4).getNickname(), Category.SERVICE, "영업소", "구입처", false)
                    );

                    Condition condition = createCondition(Sort.LATEST, null, "애플");

                    // when
                    members.forEach(member -> memberRepository.saveMember(member));

                    expectedInquiries.forEach(inquiry -> {
                        inquiryRepository.savePost(inquiry);
                        inquiryRepository.saveInquirySecretSetting(inquiry);
                    });

                    List<Inquiry> actualInquiries = inquiryRepository.findAllInquiries(condition.updatePage());

                    // 필터링 조건을 만족하는 문의만 선택
                    Predicate<Inquiry> keywordFilter = inquiry ->
                            inquiry.getTitle().contains(condition.getKeyword()) ||
                                    inquiry.getMemberNickname().contains(condition.getKeyword()) ||
                                    inquiry.getContent().contains(condition.getKeyword());

                    int expectedSize = (int) expectedInquiries.stream()
                            .filter(keywordFilter)
                            .count();

                    // then
                    assertThat(actualInquiries).hasSize(expectedSize);
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

                    List<Inquiry> expectedInquiries = LongStream.range(1L, members.size() + 1L)
                            .mapToObj(index -> createInquiryForSave(index,
                                    members.get((int) (index - 1)).getId(), members.get((int) (index - 1)).getNickname(),
                                    Category.SERVICE, "사랑", "기쁨", false))
                            .collect(Collectors.toList());

                    // when
                    members.forEach(member -> memberRepository.saveMember(member));

                    expectedInquiries.forEach(inquiry -> {
                        inquiryRepository.savePost(inquiry);
                        inquiryRepository.saveInquirySecretSetting(inquiry);
                    });


                    // then
                    assertThat(inquiryRepository.findInquiry(1L).getMemberNickname()).isEqualTo(MEMBER1_NICKNAME);
                    assertThat(inquiryRepository.findInquiry(2L).getMemberNickname()).isEqualTo(MEMBER2_NICKNAME);
                    assertThat(inquiryRepository.findInquiry(3L).getMemberNickname()).isEqualTo(MEMBER3_NICKNAME);
                    assertThat(inquiryRepository.findInquiry(4L).getMemberNickname()).isEqualTo(MEMBER4_NICKNAME);
                    assertThat(inquiryRepository.findInquiry(5L).getMemberNickname()).isEqualTo(MEMBER5_NICKNAME);

                }
            }

            @Nested
            @DisplayName("게시글을 등록할 때")
            class save {

                @Test
                @DisplayName("답변이 완료되지 않는 상태값으로 설정된 게시글로 저장된다")
                void saveInquiry() {
                    // given
                    Long postIdx = 1L;
                    Member member = createMember(MEMBER1_ID, MEMBER1_NAME, MEMBER1_NICKNAME);

                    Inquiry expectedInquiry = createInquiryForSave(1L, member.getId(), member.getNickname(),
                            Category.SERVICE, "영업소", "구입처", true, "dkanro132!");

                    // when
                    memberRepository.saveMember(member);

                    inquiryRepository.savePost(expectedInquiry);
                    inquiryRepository.saveInquirySecretSetting(expectedInquiry);

                    Inquiry actualInquiry = inquiryRepository.findInquiry(postIdx);

                    // then
                    assertThat(actualInquiry.getMemberNickname()).isEqualTo(member.getNickname());
                    assertThat(actualInquiry.getCategory()).isEqualTo(expectedInquiry.getCategory());
                    assertThat(actualInquiry.getTitle()).isEqualTo(expectedInquiry.getTitle());
                    assertThat(actualInquiry.getContent()).isEqualTo(expectedInquiry.getContent());
                    assertThat(actualInquiry.getInquiryStatus()).isEqualTo(InquiryStatus.INCOMPLETE);
                }

            }

            @Nested
            @DisplayName("게시글을 수정할 때")
            class Update {

                @Test
                @DisplayName("게시글이 수정된다")
                void updateInquiry() {
                    // given
                    Long postIdx = 1L;
                    Member member = createMember(MEMBER1_ID, MEMBER1_NAME, MEMBER1_NICKNAME);

                    Inquiry inquiry = createInquiryForSave(1L, member.getId(), member.getNickname(),
                            Category.SERVICE, "영업소", "구입처", false);

                    Inquiry expectedUpdatedInquiry = createInquiryForSave(1L, member.getId(), member.getNickname(),
                            Category.SERVICE, "슬픔", "눈물", true, "qudrnr132!")
                            .updatePostIdx(postIdx);

                    // when
                    memberRepository.saveMember(member);

                    inquiryRepository.savePost(inquiry);
                    inquiryRepository.saveInquirySecretSetting(inquiry);

                    inquiryRepository.updatePost(expectedUpdatedInquiry);
                    inquiryRepository.updateInquiry(expectedUpdatedInquiry);

                    Inquiry actualInquiry = inquiryRepository.findInquiry(postIdx);

                    // then
                    assertThat(actualInquiry.getMemberNickname()).isEqualTo(member.getNickname());
                    assertThat(actualInquiry.getCategory()).isEqualTo(expectedUpdatedInquiry.getCategory());
                    assertThat(actualInquiry.getTitle()).isEqualTo(expectedUpdatedInquiry.getTitle());
                    assertThat(actualInquiry.getContent()).isEqualTo(expectedUpdatedInquiry.getContent());
                }
            }

            @Nested
            @DisplayName("게시글을 삭제할 때")
            class Delete {

                @Test
                @DisplayName("게시글이 삭제된다")
                void deleteInquiry() {
                    // given
                    Long postIdx = 1L;
                    Member member = createMember(MEMBER1_ID, MEMBER1_NAME, MEMBER1_NICKNAME);

                    Inquiry inquiry = createInquiryForSave(1L, member.getId(), member.getNickname(),
                            Category.SERVICE, "영업소", "구입처", false);

                    // when
                    memberRepository.saveMember(member);

                    inquiryRepository.savePost(inquiry);
                    inquiryRepository.saveInquirySecretSetting(inquiry);

                    inquiryRepository.deleteInquiry(postIdx);
                    inquiryRepository.deletePost(postIdx);

                    Inquiry actualInquiry = inquiryRepository.findInquiry(postIdx);
                    List<Inquiry> expectedInquiries = inquiryRepository.findAllInquiries(createConditionOnlySort(Sort.LATEST).updatePage());

                    // then
                    assertThat(actualInquiry).isNull();
                    assertThat(expectedInquiries).isEmpty();
                }
            }
        }
    }

    @Nested
    @DisplayName("답변")
    class Answer {

        @Nested
        @DisplayName("답변을 등록할 때")
        class saveAnswer {

            @Test
            @DisplayName("답변이 완료된 상태값으로 설정된 게시글로 변경되고, 답변이 등록된다")
            void saveAnswer() {

                // given
                Long postIdx = 1L;
                Member member = createMember(MEMBER1_ID, MEMBER1_NAME, MEMBER1_NICKNAME);
                Manager manager = createManager(MANAGER1_ID, MANAGER1_NAME, MANAGER1_NICKNAME);

                Inquiry expectedInquiry = createInquiryForSave(1L, member.getId(), member.getNickname(),
                        Category.SERVICE, "영업소", "구입처", true, "dkanro132!");

                com.now.core.post.inquiry.presentation.dto.Answer expectedAnswer = createAnswer(postIdx, 1, manager.getId(), SAMPLE_ANSWER_CONTENT_1);

                // when
                memberRepository.saveMember(member);
                managerRepository.saveManager(manager);

                inquiryRepository.savePost(expectedInquiry);
                inquiryRepository.saveInquirySecretSetting(expectedInquiry);

                inquiryRepository.saveAnswer(expectedAnswer);

                Inquiry actualInquiry = inquiryRepository.findInquiry(postIdx);

                // then
                assertThat(actualInquiry.getMemberNickname()).isEqualTo(member.getNickname());
                assertThat(actualInquiry.getCategory()).isEqualTo(expectedInquiry.getCategory());
                assertThat(actualInquiry.getTitle()).isEqualTo(expectedInquiry.getTitle());
                assertThat(actualInquiry.getContent()).isEqualTo(expectedInquiry.getContent());

                assertThat(actualInquiry.getInquiryStatus()).isEqualTo(InquiryStatus.COMPLETE);
                assertThat(actualInquiry.getAnswerContent()).isEqualTo(expectedAnswer.getAnswerContent());
            }
        }

        @Nested
        @DisplayName("답변을 수정할 때")
        class updateAnswer {

            @Test
            @DisplayName("답변이 수정된다")
            void updateAnswer() {

                // given
                Long postIdx = 1L;
                Member member = createMember(MEMBER1_ID, MEMBER1_NAME, MEMBER1_NICKNAME);
                Manager manager = createManager(MANAGER1_ID, MANAGER1_NAME, MANAGER1_NICKNAME);

                Inquiry expectedInquiry = createInquiryForSave(1L, member.getId(), member.getNickname(),
                        Category.SERVICE, "영업소", "구입처", true, "dkanro132!");

                com.now.core.post.inquiry.presentation.dto.Answer answer = createAnswer(postIdx, 1, manager.getId(), SAMPLE_ANSWER_CONTENT_1);

                com.now.core.post.inquiry.presentation.dto.Answer expectedUpdateAnswer = createAnswer(postIdx, 1, manager.getId(), SAMPLE_ANSWER_CONTENT_2);

                // when
                memberRepository.saveMember(member);
                managerRepository.saveManager(manager);

                inquiryRepository.savePost(expectedInquiry);
                inquiryRepository.saveInquirySecretSetting(expectedInquiry);

                inquiryRepository.saveAnswer(answer);
                inquiryRepository.updateAnswer(expectedUpdateAnswer);

                Inquiry actualInquiry = inquiryRepository.findInquiry(postIdx);

                // then
                assertThat(actualInquiry.getMemberNickname()).isEqualTo(member.getNickname());
                assertThat(actualInquiry.getCategory()).isEqualTo(expectedInquiry.getCategory());
                assertThat(actualInquiry.getTitle()).isEqualTo(expectedInquiry.getTitle());
                assertThat(actualInquiry.getContent()).isEqualTo(expectedInquiry.getContent());

                assertThat(actualInquiry.getInquiryStatus()).isEqualTo(InquiryStatus.COMPLETE);
                assertThat(actualInquiry.getAnswerContent()).isEqualTo(expectedUpdateAnswer.getAnswerContent());
            }
        }
    }
}
