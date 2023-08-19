package com.now.core.post.domain.repository;

import com.now.config.annotations.RepositoryTest;
import com.now.core.admin.manager.domain.Manager;
import com.now.core.admin.manager.domain.ManagerRepository;
import com.now.core.category.domain.constants.Category;
import com.now.core.comment.domain.CommentRepository;
import com.now.core.post.domain.Notice;
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

import static com.now.config.fixtures.manager.ManagerFixture.*;
import static com.now.config.fixtures.post.NoticeFixture.createNoticeForSave;
import static com.now.config.fixtures.post.dto.ConditionFixture.createCondition;
import static com.now.config.fixtures.post.dto.ConditionFixture.createConditionOnlySort;
import static com.now.config.fixtures.post.dto.PostReactionFixture.createPostReaction;
import static com.now.config.utilities.SortUtils.isChronologicalOrder;
import static com.now.config.utilities.SortUtils.isFirstElementMaxAndDescending;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RepositoryTest
@DisplayName("공지 레포지토리")
class NoticeRepositoryTest {

    @Autowired
    protected PostRepository postRepository;

    @Autowired
    protected NoticeRepository noticeRepository;

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
    @DisplayName("모든 공지 게시글을 찾을 때")
    class FindAll {

        @Nested
        @DisplayName("기본적으로 상단 고정 게시글이 포함")
        class Default_Pin {

            @Test
            @DisplayName("상단 고정글이 항상 포함돼서 조회된다")
            void filter_only_category() {
                // given
                List<Manager> managers = Arrays.asList(
                        createManager(MANAGER1_ID, MANAGER1_NAME, MANAGER1_NICKNAME),
                        createManager(MANAGER2_ID, MANAGER2_NAME, MANAGER2_NICKNAME),
                        createManager(MANAGER3_ID, MANAGER3_NAME, MANAGER3_NICKNAME),
                        createManager(MANAGER4_ID, MANAGER4_NAME, MANAGER4_NICKNAME),
                        createManager(MANAGER5_ID, MANAGER5_NAME, MANAGER5_NICKNAME),
                        createManager(MANAGER6_ID, MANAGER6_NAME, MANAGER6_NICKNAME));

                List<Notice> pinnedNewsNotices = managers.stream()
                        .map(manager -> createNoticeForSave(Category.NEWS, manager.getId(), true))
                        .collect(Collectors.toList());

                List<Notice> notPinnedEventNotices = managers.stream()
                        .map(manager -> createNoticeForSave(Category.EVENT, manager.getId(), false))
                        .collect(Collectors.toList());
                List<Notice> notPinnedNewsNotices = managers.stream()
                        .map(manager -> createNoticeForSave(Category.NEWS, manager.getId(), false))
                        .collect(Collectors.toList());

                Condition condition = Condition.builder()
                        .sort(null)
                        .build()
                        .updatePage();

                // when
                managers.forEach(manager -> managerRepository.saveManager(manager));

                pinnedNewsNotices.forEach(notice -> noticeRepository.saveNotice(notice)); // Pinned NEWS Notice count : 6

                notPinnedEventNotices.forEach(notice -> noticeRepository.saveNotice(notice)); // Not Pinned EVENT Notice count : 6
                notPinnedEventNotices.forEach(notice -> noticeRepository.saveNotice(notice)); // Not Pinned EVENT Notice count : 6
                notPinnedNewsNotices.forEach(notice -> noticeRepository.saveNotice(notice)); // Not Pinned NEWS Notice count : 6
                notPinnedNewsNotices.forEach(notice -> noticeRepository.saveNotice(notice)); // Not Pinned NEWS Notice count : 6

                List<Notice> actualCommunities = noticeRepository.findAllNoticesWithPin(condition);

                int actualPinnedNoticeCount = (int) actualCommunities.stream()
                        .filter(Notice::getPinned)
                        .count();
                int actualNotPinnedNoticeCount = (int) actualCommunities.stream()
                        .filter(notice -> !notice.getPinned())
                        .count();

                // then
                assertThat(noticeRepository.findAllNoticesWithPin(new Condition())).hasSize(30); // Not Page Filter

                assertThat(actualCommunities).hasSize(pinnedNewsNotices.size() + condition.getPage().getRecordsPerPage());
                assertThat(actualPinnedNoticeCount).isEqualTo(pinnedNewsNotices.size());
                assertThat(actualNotPinnedNoticeCount).isEqualTo(condition.getPage().getRecordsPerPage());
            }
        }

        @Nested
        @DisplayName("정렬 조건")
        class Sort_of {

            @Test
            @DisplayName("최신순으로 정렬 후 조회된다")
            void filter_only_sort_latest() {
                // given
                List<Manager> managers = Arrays.asList(
                        createManager(MANAGER1_ID, MANAGER1_NAME, MANAGER1_NICKNAME),
                        createManager(MANAGER2_ID, MANAGER2_NAME, MANAGER2_NICKNAME),
                        createManager(MANAGER3_ID, MANAGER3_NAME, MANAGER3_NICKNAME),
                        createManager(MANAGER4_ID, MANAGER4_NAME, MANAGER4_NICKNAME),
                        createManager(MANAGER5_ID, MANAGER5_NAME, MANAGER5_NICKNAME),
                        createManager(MANAGER6_ID, MANAGER6_NAME, MANAGER6_NICKNAME));

                List<Notice> notPinnedEventNotices = managers.stream()
                        .map(manager -> createNoticeForSave(Category.EVENT, manager.getId(), false))
                        .collect(Collectors.toList());

                Condition condition = createConditionOnlySort(Sort.LATEST);

                // when
                managers.forEach(manager -> managerRepository.saveManager(manager));
                notPinnedEventNotices.forEach(notice -> noticeRepository.saveNotice(notice));

                List<Notice> actualNotices = noticeRepository.findAllNoticesWithPin(condition);

                List<LocalDateTime> regDateTimes = actualNotices.stream()
                        .map(Notice::getRegDate)
                        .collect(Collectors.toList());

                // then
                assertThat(actualNotices).hasSize(notPinnedEventNotices.size());
                assertTrue(isChronologicalOrder(regDateTimes));
            }

            @Test
            @DisplayName("추천순(좋아요-싫어요)으로 정렬 후 조회된다")
            void filter_only_sort_recommended() {
                // given
                List<Manager> managers = Arrays.asList(
                        createManager(MANAGER1_ID, MANAGER1_NAME, MANAGER1_NICKNAME),
                        createManager(MANAGER2_ID, MANAGER2_NAME, MANAGER2_NICKNAME),
                        createManager(MANAGER3_ID, MANAGER3_NAME, MANAGER3_NICKNAME),
                        createManager(MANAGER4_ID, MANAGER4_NAME, MANAGER4_NICKNAME),
                        createManager(MANAGER5_ID, MANAGER5_NAME, MANAGER5_NICKNAME),
                        createManager(MANAGER6_ID, MANAGER6_NAME, MANAGER6_NICKNAME));

                List<Notice> notPinnedEventNotices = managers.stream()
                        .map(manager -> createNoticeForSave(Category.EVENT, manager.getId(), false))
                        .collect(Collectors.toList());

                Condition condition = createConditionOnlySort(Sort.RECOMMENDED);

                List<PostReaction> postLikeReactions = Arrays.asList(
                        createPostReaction(1L, 1, Reaction.LIKE),
                        createPostReaction(1L, 2, Reaction.LIKE),
                        createPostReaction(1L, 3, Reaction.LIKE),

                        createPostReaction(2L, 2, Reaction.LIKE),
                        createPostReaction(2L, 1, Reaction.LIKE),

                        createPostReaction(3L, 3, Reaction.LIKE),

                        createPostReaction(4L, 4, Reaction.LIKE),
                        createPostReaction(4L, 1, Reaction.LIKE),

                        createPostReaction(5L, 5, Reaction.LIKE)
                );

                List<PostReaction> postUnlikeReactions = Arrays.asList(
                        createPostReaction(1L, 2, Reaction.UNLIKE),
                        createPostReaction(1L, 3, Reaction.UNLIKE),

                        createPostReaction(2L, 2, Reaction.UNLIKE),
                        createPostReaction(2L, 1, Reaction.UNLIKE)
                );

                // when
                managers.forEach(member -> managerRepository.saveManager(member));
                notPinnedEventNotices.forEach(notice -> noticeRepository.saveNotice(notice));
                postLikeReactions.forEach(postReaction -> postRepository.savePostReaction(postReaction));
                postLikeReactions.forEach(postReaction -> postRepository.incrementLikeCount(postReaction.getPostIdx()));

                postUnlikeReactions.forEach(postReaction -> postRepository.updatePostReaction(postReaction));
                postUnlikeReactions.forEach(postReaction -> postRepository.decrementLikeCount(postReaction.getPostIdx()));

                List<Notice> actualNotices = noticeRepository.findAllNoticesWithPin(condition.updatePage());

                List<Integer> likeMinusDislikeCounts = actualNotices.stream()
                        .map(notice -> notice.getLikeCount() - notice.getDislikeCount())
                        .collect(Collectors.toList());

                // then
                assertThat(actualNotices).hasSize(notPinnedEventNotices.size());
                assertTrue(isFirstElementMaxAndDescending(likeMinusDislikeCounts));
            }

            @Test
            @DisplayName("조회수순으로 정렬 후 조회된다")
            void filter_only_sort_most_viewed() {
                // given
                List<Manager> managers = Arrays.asList(
                        createManager(MANAGER1_ID, MANAGER1_NAME, MANAGER1_NICKNAME),
                        createManager(MANAGER2_ID, MANAGER2_NAME, MANAGER2_NICKNAME),
                        createManager(MANAGER3_ID, MANAGER3_NAME, MANAGER3_NICKNAME),
                        createManager(MANAGER4_ID, MANAGER4_NAME, MANAGER4_NICKNAME),
                        createManager(MANAGER5_ID, MANAGER5_NAME, MANAGER5_NICKNAME),
                        createManager(MANAGER6_ID, MANAGER6_NAME, MANAGER6_NICKNAME));

                List<Notice> notPinnedEventNotices = managers.stream()
                        .map(manager -> createNoticeForSave(Category.EVENT, manager.getId(), false))
                        .collect(Collectors.toList());

                Condition condition = createConditionOnlySort(Sort.MOST_VIEWED);

                // when
                managers.forEach(member -> managerRepository.saveManager(member));
                notPinnedEventNotices.forEach(notice -> noticeRepository.saveNotice(notice));

                List<Long> expectedPostIndexes = notPinnedEventNotices.stream()
                        .map(Notice::getPostIdx)
                        .collect(Collectors.toList());
                randomIncreaseViewCount(postRepository, expectedPostIndexes, new int[]{2, 3, 1, 2, 1, 10});

                List<Notice> actualNotices = noticeRepository.findAllNoticesWithPin(condition.updatePage());

                List<Integer> viewCounts = actualNotices.stream()
                        .map(Notice::getViewCount)
                        .collect(Collectors.toList());

                // then
                assertThat(actualNotices).hasSize(notPinnedEventNotices.size());
                assertTrue(isFirstElementMaxAndDescending(viewCounts));
            }
        }

        @Nested
        @DisplayName("카테고리")
        class Category_of {

            @Test
            @DisplayName("특정 카테고리의 게시글만 조회된다")
            void filter_only_category() {
                // given
                List<Manager> managers = Arrays.asList(
                        createManager(MANAGER1_ID, MANAGER1_NAME, MANAGER1_NICKNAME),
                        createManager(MANAGER2_ID, MANAGER2_NAME, MANAGER2_NICKNAME),
                        createManager(MANAGER3_ID, MANAGER3_NAME, MANAGER3_NICKNAME),
                        createManager(MANAGER4_ID, MANAGER4_NAME, MANAGER4_NICKNAME),
                        createManager(MANAGER5_ID, MANAGER5_NAME, MANAGER5_NICKNAME),
                        createManager(MANAGER6_ID, MANAGER6_NAME, MANAGER6_NICKNAME));

                List<Notice> expectedNotices = Arrays.asList(
                        createNoticeForSave(managers.get(0).getId(), Category.EVENT, false),
                        createNoticeForSave(managers.get(1).getId(), Category.NEWS, false),
                        createNoticeForSave(managers.get(2).getId(), Category.EVENT, false),
                        createNoticeForSave(managers.get(3).getId(), Category.NEWS, false),
                        createNoticeForSave(managers.get(4).getId(), Category.EVENT, false),
                        createNoticeForSave(managers.get(5).getId(), Category.NEWS, false)
                );

                Condition condition = createCondition(Sort.LATEST, Category.NEWS);

                // when
                managers.forEach(member -> managerRepository.saveManager(member));
                expectedNotices.forEach(community -> noticeRepository.saveNotice(community));
                List<Notice> actualNotices = noticeRepository.findAllNoticesWithPin(condition.updatePage());

                int expectedSize = (int) expectedNotices.stream()
                        .filter(notice ->
                                notice.getCategory() == condition.getCategory()).count();

                List<LocalDateTime> regDateTimes = actualNotices.stream()
                        .map(Notice::getRegDate)
                        .collect(Collectors.toList());

                // then
                assertThat(actualNotices).hasSize(expectedSize);
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
                List<Manager> managers = Arrays.asList(
                        createManager(MANAGER1_ID, MANAGER1_NAME, "메가슈퍼"),
                        createManager(MANAGER2_ID, MANAGER2_NAME, "애플매니저"),
                        createManager(MANAGER3_ID, MANAGER3_NAME, "appleManager"),
                        createManager(MANAGER4_ID, MANAGER4_NAME, "바나매니저"),
                        createManager(MANAGER5_ID, MANAGER5_NAME, "bananaManager"),
                        createManager(MANAGER6_ID, MANAGER6_NAME, "멜론매니저"));

                List<Notice> notices = Arrays.asList(
                        createNoticeForSave(managers.get(0).getId(), 1, managers.get(0).getNickname(), Category.NEWS, "사랑", "기쁨", false),
                        createNoticeForSave(managers.get(1).getId(), 2, managers.get(1).getNickname(), Category.EVENT, "love", "안녕", false),
                        createNoticeForSave(managers.get(2).getId(), 3, managers.get(2).getNickname(), Category.NEWS, "탄수화물", "식이섬유", false),
                        createNoticeForSave(managers.get(3).getId(), 4, managers.get(3).getNickname(), Category.EVENT, "단백질", "운동", false),
                        createNoticeForSave(managers.get(4).getId(), 4, managers.get(4).getNickname(), Category.NEWS, "영업소", "구입처", false),
                        createNoticeForSave(managers.get(5).getId(), 5, managers.get(5).getNickname(), Category.EVENT, "Charlie", "Puth", false)
                );

                Condition condition = createCondition(Sort.LATEST, null, "구입처");

                // when
                managers.forEach(member -> managerRepository.saveManager(member));
                notices.forEach(community -> noticeRepository.saveNotice(community));
                List<Notice> actualNotices = noticeRepository.findAllNoticesWithPin(condition.updatePage());

                // 필터링 조건을 만족하는 커뮤니티만 선택
                Predicate<Notice> keywordFilter = notice ->
                        notice.getTitle().contains(condition.getKeyword()) ||
                                notice.getManagerNickname().contains(condition.getKeyword()) ||
                                notice.getContent().contains(condition.getKeyword());

                int expectedSize = (int) notices.stream()
                        .filter(keywordFilter)
                        .count();

                // then
                assertThat(actualNotices).hasSize(expectedSize);
            }

            @Test
            @DisplayName("특정 키워드가 제목, 닉네임, 내용 중 하나 혹은 여러 항목에 포함된 게시글만 조회된다")
            void filter_only_keyword_match_title() {
                // given
                List<Manager> managers = Arrays.asList(
                        createManager(MANAGER1_ID, MANAGER1_NAME, "메가슈퍼"),
                        createManager(MANAGER2_ID, MANAGER2_NAME, "애플매니저"),
                        createManager(MANAGER3_ID, MANAGER3_NAME, "appleManager"),
                        createManager(MANAGER4_ID, MANAGER4_NAME, "바나매니저"),
                        createManager(MANAGER5_ID, MANAGER5_NAME, "bananaManager"),
                        createManager(MANAGER6_ID, MANAGER6_NAME, "멜론매니저"));

                List<Notice> notices = Arrays.asList(
                        createNoticeForSave(managers.get(0).getId(), 1, managers.get(0).getNickname(), Category.NEWS, "사랑", "기쁨", false),
                        createNoticeForSave(managers.get(1).getId(), 2, managers.get(1).getNickname(), Category.EVENT, "love", "안녕", false),
                        createNoticeForSave(managers.get(2).getId(), 3, managers.get(2).getNickname(), Category.NEWS, "탄수화물", "식이섬유", false),
                        createNoticeForSave(managers.get(3).getId(), 4, managers.get(3).getNickname(), Category.EVENT, "단백질", "운동", false),
                        createNoticeForSave(managers.get(4).getId(), 4, managers.get(4).getNickname(), Category.NEWS, "영업소", "구입처", false),
                        createNoticeForSave(managers.get(5).getId(), 5, managers.get(5).getNickname(), Category.EVENT, "Charlie", "Puth", false)
                );

                Condition condition = createCondition(Sort.LATEST, null, "Charlie");

                // when
                managers.forEach(member -> managerRepository.saveManager(member));
                notices.forEach(community -> noticeRepository.saveNotice(community));
                List<Notice> actualNotices = noticeRepository.findAllNoticesWithPin(condition.updatePage());

                // 필터링 조건을 만족하는 커뮤니티만 선택
                Predicate<Notice> keywordFilter = notice ->
                        notice.getTitle().contains(condition.getKeyword()) ||
                                notice.getManagerNickname().contains(condition.getKeyword()) ||
                                notice.getContent().contains(condition.getKeyword());

                int expectedSize = (int) notices.stream()
                        .filter(keywordFilter)
                        .count();

                // then
                assertThat(actualNotices).hasSize(expectedSize);
            }

            @Test
            @DisplayName("특정 키워드가 제목, 닉네임, 내용 중 하나 혹은 여러 항목에 포함된 게시글만 조회된다")
            void filter_only_keyword_match_managerNickname() {
                // given
                List<Manager> managers = Arrays.asList(
                        createManager(MANAGER1_ID, MANAGER1_NAME, "메가슈퍼"),
                        createManager(MANAGER2_ID, MANAGER2_NAME, "애플매니저"),
                        createManager(MANAGER3_ID, MANAGER3_NAME, "appleManager"),
                        createManager(MANAGER4_ID, MANAGER4_NAME, "바나매니저"),
                        createManager(MANAGER5_ID, MANAGER5_NAME, "bananaManager"),
                        createManager(MANAGER6_ID, MANAGER6_NAME, "멜론매니저"));

                List<Notice> notices = Arrays.asList(
                        createNoticeForSave(managers.get(0).getId(), 1, managers.get(0).getNickname(), Category.NEWS, "사랑", "기쁨", false),
                        createNoticeForSave(managers.get(1).getId(), 2, managers.get(1).getNickname(), Category.EVENT, "love", "안녕", false),
                        createNoticeForSave(managers.get(2).getId(), 3, managers.get(2).getNickname(), Category.NEWS, "탄수화물", "식이섬유", false),
                        createNoticeForSave(managers.get(3).getId(), 4, managers.get(3).getNickname(), Category.EVENT, "단백질", "운동", false),
                        createNoticeForSave(managers.get(4).getId(), 4, managers.get(4).getNickname(), Category.NEWS, "영업소", "구입처", false),
                        createNoticeForSave(managers.get(5).getId(), 5, managers.get(5).getNickname(), Category.EVENT, "Charlie", "Puth", false)
                );

                Condition condition = createCondition(Sort.LATEST, null, "ba");

                // when
                managers.forEach(member -> managerRepository.saveManager(member));
                notices.forEach(community -> noticeRepository.saveNotice(community));
                List<Notice> actualNotices = noticeRepository.findAllNoticesWithPin(condition.updatePage());

                // 필터링 조건을 만족하는 커뮤니티만 선택
                Predicate<Notice> keywordFilter = notice ->
                        notice.getTitle().contains(condition.getKeyword()) ||
                                notice.getManagerNickname().contains(condition.getKeyword()) ||
                                notice.getContent().contains(condition.getKeyword());

                int expectedSize = (int) notices.stream()
                        .filter(keywordFilter)
                        .count();

                // then
                assertThat(actualNotices).hasSize(expectedSize);
            }
        }

        @Nested
        @DisplayName("단건 게시글을 찾을 때")
        class FindOne {

            @Test
            @DisplayName("게시글 번호로 조회된다")
            void by_postIdx() {
                // given
                List<Manager> managers = Arrays.asList(
                        createManager(MANAGER1_ID, MANAGER1_NAME, MANAGER1_NICKNAME),
                        createManager(MANAGER2_ID, MANAGER2_NAME, MANAGER2_NICKNAME),
                        createManager(MANAGER3_ID, MANAGER3_NAME, MANAGER3_NICKNAME),
                        createManager(MANAGER4_ID, MANAGER4_NAME, MANAGER4_NICKNAME),
                        createManager(MANAGER5_ID, MANAGER5_NAME, MANAGER5_NICKNAME),
                        createManager(MANAGER6_ID, MANAGER6_NAME, MANAGER6_NICKNAME));

                List<Notice> expectedNotices = LongStream.range(1, managers.size() + 1)
                        .mapToObj(index -> createNoticeForSave((int) index, managers.get((int) (index - 1)).getId(), false))
                        .collect(Collectors.toList());

                managers.forEach(member -> managerRepository.saveManager(member));
                expectedNotices.forEach(notice -> noticeRepository.saveNotice(notice));

                assertThat(noticeRepository.findNotice(1L).getManagerNickname()).isEqualTo(MANAGER1_NICKNAME);
                assertThat(noticeRepository.findNotice(2L).getManagerNickname()).isEqualTo(MANAGER2_NICKNAME);
                assertThat(noticeRepository.findNotice(3L).getManagerNickname()).isEqualTo(MANAGER3_NICKNAME);
                assertThat(noticeRepository.findNotice(4L).getManagerNickname()).isEqualTo(MANAGER4_NICKNAME);
                assertThat(noticeRepository.findNotice(5L).getManagerNickname()).isEqualTo(MANAGER5_NICKNAME);
                assertThat(noticeRepository.findNotice(6L).getManagerNickname()).isEqualTo(MANAGER6_NICKNAME);
            }
        }

        @Nested
        @DisplayName("게시글을 수정할 때")
        class Update {

            @Test
            @DisplayName("게시글이 수정된다")
            void updateNotice() {
                // given
                Long postIdx = 1L;
                Manager manager = createManager(MANAGER1_ID, MANAGER1_NAME, MANAGER1_NICKNAME);

                Notice notice = createNoticeForSave(manager.getId(), 1, manager.getNickname(), Category.NEWS, "사랑", "기쁨", false);
                Notice expectedUpdatedNotice = createNoticeForSave(manager.getId(), 1, 
                        manager.getNickname(), Category.EVENT, "슬픔", "눈물", true).updatePostIdx(postIdx);

                // when
                managerRepository.saveManager(manager);
                noticeRepository.saveNotice(notice);
                noticeRepository.updateNotice(expectedUpdatedNotice);

                Notice actualNotice = noticeRepository.findNotice(postIdx);

                // then
                assertThat(actualNotice.getManagerNickname()).isEqualTo(manager.getNickname());
                assertThat(actualNotice.getCategory()).isEqualTo(expectedUpdatedNotice.getCategory());
                assertThat(actualNotice.getTitle()).isEqualTo(expectedUpdatedNotice.getTitle());
                assertThat(actualNotice.getContent()).isEqualTo(expectedUpdatedNotice.getContent());
                assertThat(actualNotice.getPinned()).isEqualTo(expectedUpdatedNotice.getPinned());
            }
        }

        @Nested
        @DisplayName("게시글을 삭제할 때")
        class Delete {

            @Test
            @DisplayName("게시글이 삭제된다")
            void deleteNotice() {
                // given
                Long postIdx = 1L;
                Manager manager = createManager(MANAGER1_ID, MANAGER1_NAME, MANAGER1_NICKNAME);

                Notice notice = createNoticeForSave(manager.getId(), 1, manager.getNickname(),
                                                            Category.NEWS, "사랑", "기쁨", false);

                // when
                managerRepository.saveManager(manager);
                noticeRepository.saveNotice(notice);
                noticeRepository.deleteNotice(postIdx);

                Notice actualNotice = noticeRepository.findNotice(postIdx);

                List<Notice> actualCommunities = noticeRepository.findAllNoticesWithPin(createConditionOnlySort(Sort.LATEST).updatePage());

                // then
                assertThat(actualNotice).isNull();
                assertThat(actualCommunities).isEmpty();
            }
        }
    }
}
