package com.now.core.post.photo.domain.repository;

import com.now.config.annotations.RepositoryTest;
import com.now.core.attachment.domain.AttachmentRepository;
import com.now.core.category.domain.constants.Category;
import com.now.core.comment.domain.CommentRepository;
import com.now.core.member.domain.Member;
import com.now.core.member.domain.MemberRepository;
import com.now.core.post.common.domain.repository.PostRepository;
import com.now.core.post.photo.domain.Photo;
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

import static com.now.config.fixtures.member.MemberFixture.*;
import static com.now.config.fixtures.post.PhotoFixture.createPhotoForSave;
import static com.now.config.fixtures.post.dto.ConditionFixture.createCondition;
import static com.now.config.fixtures.post.dto.ConditionFixture.createConditionOnlySort;
import static com.now.config.fixtures.post.dto.PostReactionFixture.createPostReaction;
import static com.now.config.utilities.SortUtils.isChronologicalOrder;
import static com.now.config.utilities.SortUtils.isFirstElementMaxAndDescending;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RepositoryTest
@DisplayName("사진 레포지토리")
class PhotoRepositoryTest {

    @Autowired
    protected PostRepository postRepository;

    @Autowired
    protected PhotoRepository photoRepository;

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
    @DisplayName("모든 사진 게시글을 찾을 때")
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

                List<Photo> expectedPhotos = Arrays.asList(
                        createPhotoForSave(1L, members.get(0).getId(), members.get(0).getNickname(), Category.DAILY_LIFE, "사랑", "기쁨"),
                        createPhotoForSave(2L, members.get(1).getId(), members.get(1).getNickname(), Category.ARTWORK, "love", "안녕"),
                        createPhotoForSave(3L, members.get(2).getId(), members.get(2).getNickname(), Category.DAILY_LIFE, "탄수화물", "식이섬유"),
                        createPhotoForSave(4L, members.get(3).getId(), members.get(3).getNickname(), Category.ARTWORK, "단백질", "운동"),
                        createPhotoForSave(5L, members.get(4).getId(), members.get(4).getNickname(), Category.DAILY_LIFE, "영업소", "구입처")
                );

                Condition condition = createConditionOnlySort(Sort.LATEST);

                // when
                members.forEach(member -> memberRepository.saveMember(member));
                expectedPhotos.forEach(photo -> photoRepository.savePhoto(photo));
                List<Photo> actualPhotos = photoRepository.findAllPhotos(condition.updatePage());

                List<LocalDateTime> actualRegDateTimes = actualPhotos.stream()
                        .map(Photo::getRegDate)
                        .collect(Collectors.toList());

                // then
                assertThat(actualPhotos).hasSize(expectedPhotos.size());
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

                List<Photo> expectedPhotos = Arrays.asList(
                        createPhotoForSave(1L, members.get(0).getId(), members.get(0).getNickname(), Category.DAILY_LIFE, "사랑", "기쁨"),
                        createPhotoForSave(2L, members.get(1).getId(), members.get(1).getNickname(), Category.ARTWORK, "love", "안녕"),
                        createPhotoForSave(3L, members.get(2).getId(), members.get(2).getNickname(), Category.DAILY_LIFE, "탄수화물", "식이섬유"),
                        createPhotoForSave(4L, members.get(3).getId(), members.get(3).getNickname(), Category.ARTWORK, "단백질", "운동"),
                        createPhotoForSave(5L, members.get(4).getId(), members.get(4).getNickname(), Category.DAILY_LIFE, "영업소", "구입처")
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
                expectedPhotos.forEach(photo -> photoRepository.savePhoto(photo));
                postLikeReactions.forEach(postReaction -> postRepository.savePostReaction(postReaction));
                postLikeReactions.forEach(postReaction -> postRepository.incrementLikeCount(postReaction.getPostIdx()));

                postUnlikeReactions.forEach(postReaction -> postRepository.updatePostReaction(postReaction));
                postUnlikeReactions.forEach(postReaction -> postRepository.decrementLikeCount(postReaction.getPostIdx()));

                List<Photo> actualPhotos = photoRepository.findAllPhotos(condition.updatePage());

                List<Integer> likeMinusDislikeCounts = actualPhotos.stream()
                        .map(photo -> photo.getLikeCount() - photo.getDislikeCount())
                        .collect(Collectors.toList());

                // then
                assertThat(actualPhotos).hasSize(expectedPhotos.size());
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

                List<Photo> expectedPhotos = Arrays.asList(
                        createPhotoForSave(1L, members.get(0).getId(), members.get(0).getNickname(), Category.DAILY_LIFE, "사랑", "기쁨"),
                        createPhotoForSave(2L, members.get(1).getId(), members.get(1).getNickname(), Category.ARTWORK, "love", "안녕"),
                        createPhotoForSave(3L, members.get(2).getId(), members.get(2).getNickname(), Category.DAILY_LIFE, "탄수화물", "식이섬유"),
                        createPhotoForSave(4L, members.get(3).getId(), members.get(3).getNickname(), Category.ARTWORK, "단백질", "운동"),
                        createPhotoForSave(5L, members.get(4).getId(), members.get(4).getNickname(), Category.DAILY_LIFE, "영업소", "구입처")
                );

                Condition condition = createConditionOnlySort(Sort.MOST_VIEWED);

                // when
                members.forEach(member -> memberRepository.saveMember(member));
                expectedPhotos.forEach(photo -> photoRepository.savePhoto(photo));

                List<Long> expectedPostIndexes = expectedPhotos.stream()
                        .map(Photo::getPostIdx)
                        .collect(Collectors.toList());
                randomIncreaseViewCount(postRepository, expectedPostIndexes, new int[]{2, 3, 1, 2, 1});

                List<Photo> actualPhotos = photoRepository.findAllPhotos(condition.updatePage());

                List<Integer> viewCounts = actualPhotos.stream()
                        .map(Photo::getViewCount)
                        .collect(Collectors.toList());

                // then
                assertThat(actualPhotos).hasSize(expectedPhotos.size());
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

                List<Photo> expectedPhotos = Arrays.asList(
                        createPhotoForSave(1L, members.get(0).getId(), members.get(0).getNickname(), Category.DAILY_LIFE, "사랑", "기쁨"),
                        createPhotoForSave(2L, members.get(1).getId(), members.get(1).getNickname(), Category.ARTWORK, "love", "안녕"),
                        createPhotoForSave(3L, members.get(2).getId(), members.get(2).getNickname(), Category.DAILY_LIFE, "탄수화물", "식이섬유"),
                        createPhotoForSave(4L, members.get(3).getId(), members.get(3).getNickname(), Category.ARTWORK, "단백질", "운동"),
                        createPhotoForSave(5L, members.get(4).getId(), members.get(4).getNickname(), Category.DAILY_LIFE, "영업소", "구입처")
                );

                Condition condition = createCondition(Sort.LATEST, Category.LIFESTYLE);

                // when
                members.forEach(member -> memberRepository.saveMember(member));
                expectedPhotos.forEach(photo -> photoRepository.savePhoto(photo));
                List<Photo> actualPhotos = photoRepository.findAllPhotos(condition.updatePage());

                int expectedSize = (int) expectedPhotos.stream()
                        .filter(photo -> photo.getCategory() == condition.getCategory()).count();

                List<LocalDateTime> regDateTimes = actualPhotos.stream()
                        .map(Photo::getRegDate)
                        .collect(Collectors.toList());

                // then
                assertThat(actualPhotos).hasSize(expectedSize);
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

                List<Photo> expectedPhotos = Arrays.asList(
                        createPhotoForSave(1L, members.get(0).getId(), members.get(0).getNickname(), Category.DAILY_LIFE, "사랑", "기쁨"),
                        createPhotoForSave(2L, members.get(1).getId(), members.get(1).getNickname(), Category.ARTWORK, "love", "안녕"),
                        createPhotoForSave(3L, members.get(2).getId(), members.get(2).getNickname(), Category.DAILY_LIFE, "탄수화물", "식이섬유"),
                        createPhotoForSave(4L, members.get(3).getId(), members.get(3).getNickname(), Category.ARTWORK, "단백질", "운동"),
                        createPhotoForSave(5L, members.get(4).getId(), members.get(4).getNickname(), Category.DAILY_LIFE, "영업소", "구입처")
                );

                Condition condition = createCondition(Sort.LATEST, null, "구입처");

                // when
                members.forEach(member -> memberRepository.saveMember(member));
                expectedPhotos.forEach(photo -> photoRepository.savePhoto(photo));
                List<Photo> actualPhotos = photoRepository.findAllPhotos(condition.updatePage());

                // 필터링 조건을 만족하는 사진만 선택
                Predicate<Photo> keywordFilter = photo ->
                        photo.getTitle().contains(condition.getKeyword()) ||
                                photo.getMemberNickname().contains(condition.getKeyword()) ||
                                photo.getContent().contains(condition.getKeyword());

                int expectedSize = (int) expectedPhotos.stream()
                        .filter(keywordFilter)
                        .count();

                // then
                assertThat(actualPhotos).hasSize(expectedSize);
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

                List<Photo> expectedPhotos = Arrays.asList(
                        createPhotoForSave(1L, members.get(0).getId(), members.get(0).getNickname(), Category.DAILY_LIFE, "사랑", "기쁨"),
                        createPhotoForSave(2L, members.get(1).getId(), members.get(1).getNickname(), Category.ARTWORK, "love", "안녕"),
                        createPhotoForSave(3L, members.get(2).getId(), members.get(2).getNickname(), Category.DAILY_LIFE, "탄수화물", "식이섬유"),
                        createPhotoForSave(4L, members.get(3).getId(), members.get(3).getNickname(), Category.ARTWORK, "단백질", "운동"),
                        createPhotoForSave(5L, members.get(4).getId(), members.get(4).getNickname(), Category.DAILY_LIFE, "영업소", "구입처")
                );
                Condition condition = createCondition(Sort.LATEST, null, "lo");

                // when
                members.forEach(member -> memberRepository.saveMember(member));
                expectedPhotos.forEach(photo -> photoRepository.savePhoto(photo));
                List<Photo> actualPhotos = photoRepository.findAllPhotos(condition.updatePage());

                // 필터링 조건을 만족하는 사진만 선택
                Predicate<Photo> keywordFilter = photo ->
                        photo.getTitle().contains(condition.getKeyword()) ||
                                photo.getMemberNickname().contains(condition.getKeyword()) ||
                                photo.getContent().contains(condition.getKeyword());

                int expectedSize = (int) expectedPhotos.stream()
                        .filter(keywordFilter)
                        .count();

                // then
                assertThat(actualPhotos).hasSize(expectedSize);
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

                List<Photo> expectedPhotos = Arrays.asList(
                        createPhotoForSave(1L, members.get(0).getId(), members.get(0).getNickname(), Category.DAILY_LIFE, "사랑", "기쁨"),
                        createPhotoForSave(2L, members.get(1).getId(), members.get(1).getNickname(), Category.ARTWORK, "love", "안녕"),
                        createPhotoForSave(3L, members.get(2).getId(), members.get(2).getNickname(), Category.DAILY_LIFE, "탄수화물", "식이섬유"),
                        createPhotoForSave(4L, members.get(3).getId(), members.get(3).getNickname(), Category.ARTWORK, "단백질", "운동"),
                        createPhotoForSave(5L, members.get(4).getId(), members.get(4).getNickname(), Category.DAILY_LIFE, "영업소", "구입처")
                );

                Condition condition = createCondition(Sort.LATEST, null, "애플");

                // when
                members.forEach(member -> memberRepository.saveMember(member));
                expectedPhotos.forEach(photo -> photoRepository.savePhoto(photo));
                List<Photo> actualPhotos = photoRepository.findAllPhotos(condition.updatePage());

                // 필터링 조건을 만족하는 사진만 선택
                Predicate<Photo> keywordFilter = photo ->
                        photo.getTitle().contains(condition.getKeyword()) ||
                                photo.getMemberNickname().contains(condition.getKeyword()) ||
                                photo.getContent().contains(condition.getKeyword());

                int expectedSize = (int) expectedPhotos.stream()
                        .filter(keywordFilter)
                        .count();

                // then
                assertThat(actualPhotos).hasSize(expectedSize);
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

                List<Photo> expectedPhotos = LongStream.range(1L, members.size() + 1L)
                        .mapToObj(index -> createPhotoForSave(index,
                                members.get((int) (index - 1)).getId(), members.get((int) (index - 1)).getNickname(),
                                Category.DAILY_LIFE, "영업소", "구입처"))
                        .collect(Collectors.toList());


                // when
                members.forEach(member -> memberRepository.saveMember(member));
                expectedPhotos.forEach(photo -> photoRepository.savePhoto(photo));


                // then
                assertThat(photoRepository.findPhoto(1L).getMemberNickname()).isEqualTo(MEMBER1_NICKNAME);
                assertThat(photoRepository.findPhoto(2L).getMemberNickname()).isEqualTo(MEMBER2_NICKNAME);
                assertThat(photoRepository.findPhoto(3L).getMemberNickname()).isEqualTo(MEMBER3_NICKNAME);
                assertThat(photoRepository.findPhoto(4L).getMemberNickname()).isEqualTo(MEMBER4_NICKNAME);
                assertThat(photoRepository.findPhoto(5L).getMemberNickname()).isEqualTo(MEMBER5_NICKNAME);

            }
        }

        @Nested
        @DisplayName("게시글을 수정할 때")
        class Update {

            @Test
            @DisplayName("게시글이 수정된다")
            void updatePhoto() {
                // given
                Long postIdx = 1L;
                Member member = createMember(MEMBER1_ID, MEMBER1_NAME, MEMBER1_NICKNAME);

                Photo photo = createPhotoForSave(1L, member.getId(), member.getNickname(),
                                                                    Category.DAILY_LIFE, "영업소", "구입처");

                Photo expectedUpdatedPhoto = createPhotoForSave(1L, member.getId(), member.getNickname(),
                        Category.LIFESTYLE, "슬픔", "눈물")
                        .updatePostIdx(postIdx);

                // when
                memberRepository.saveMember(member);
                photoRepository.savePhoto(photo);
                photoRepository.updatePhoto(expectedUpdatedPhoto);

                Photo actualPhoto = photoRepository.findPhoto(postIdx);

                // then
                assertThat(actualPhoto.getMemberNickname()).isEqualTo(member.getNickname());
                assertThat(actualPhoto.getCategory()).isEqualTo(expectedUpdatedPhoto.getCategory());
                assertThat(actualPhoto.getTitle()).isEqualTo(expectedUpdatedPhoto.getTitle());
                assertThat(actualPhoto.getContent()).isEqualTo(expectedUpdatedPhoto.getContent());
            }
        }

        @Nested
        @DisplayName("게시글을 삭제할 때")
        class Delete {

            @Test
            @DisplayName("게시글이 삭제된다")
            void deletePhoto() {
                // given
                Long postIdx = 1L;
                Member member = createMember(MEMBER1_ID, MEMBER1_NAME, MEMBER1_NICKNAME);

                Photo photo = createPhotoForSave(1L, member.getId(), member.getNickname(),
                                                            Category.DAILY_LIFE, "영업소", "구입처");

                // when
                memberRepository.saveMember(member);
                photoRepository.savePhoto(photo);
                photoRepository.deletePhoto(postIdx);
                Photo actualPhoto = photoRepository.findPhoto(postIdx);
                List<Photo> expectedPhotos = photoRepository.findAllPhotos(createConditionOnlySort(Sort.LATEST).updatePage());

                // then
                assertThat(actualPhoto).isNull();
                assertThat(expectedPhotos).isEmpty();
            }
        }
    }
}
