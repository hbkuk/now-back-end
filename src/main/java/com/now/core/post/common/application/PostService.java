package com.now.core.post.common.application;

import com.now.common.exception.ErrorType;
import com.now.core.member.domain.Member;
import com.now.core.member.domain.MemberRepository;
import com.now.core.member.exception.InvalidMemberException;
import com.now.core.post.common.domain.repository.PostRepository;
import com.now.core.post.common.exception.CannotUpdateReactionException;
import com.now.core.post.common.exception.InvalidPostException;
import com.now.core.post.common.presentation.dto.Condition;
import com.now.core.post.common.presentation.dto.PostReaction;
import com.now.core.post.common.presentation.dto.PostReactionResponse;
import com.now.core.post.common.presentation.dto.Posts;
import com.now.core.post.common.presentation.dto.constants.Reaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * 커뮤니티 게시글 관련 비즈니스 로직을 처리하는 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    /**
     * 모든 커뮤니티 게시글 정보를 조회 후 반환
     *
     * @return 커뮤니티 게시글 정보 리스트
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "postCache", key = "#condition.maxNumberOfPosts")
    public List<Posts> getAllPosts(Condition condition) {
        log.debug("Fetching posts from the database...");
        return postRepository.findAllPosts(condition);
    }

    /**
     * 조건에 맞는 게시물을 조회 후 수량 반환
     *
     * @param condition 조건 객체
     * @return 조건에 맞는 게시물을 조회 후 수량 반환
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "postCache", key = "#condition.hashCode()")
    public Long getTotalPostCount(Condition condition) {
        return postRepository.findTotalPostCount(condition);
    }

    /**
     * 특정 게시글에 대한 회원의 반응 정보를 조회 후 반환
     *
     * @param postIdx           게시글 번호
     * @param memberId          회원 아이디
     * @param isReactionDetails 반응에 대한 상세 정보 반환 여부
     * @return 반응 정보를 포함한 {@link PostReactionResponse} 객체
     */
    @Transactional(readOnly = true)
    public PostReactionResponse getPostReaction(Long postIdx, String memberId, boolean isReactionDetails) {
        Member member = getMember(memberId);

        if (!isExistPost(postIdx)) {
            throw new InvalidPostException(ErrorType.NOT_FOUND_POST);
        }

        if (!isReactionDetails) {
            return getPostReaction(postIdx, member.getMemberIdx());
        }
        return getPostReactionDetails(postIdx, member.getMemberIdx());
    }

    /**
     * 반응 정보 저장
     *
     * @param newPostReaction 저장할 반응 정보를 포함하는 객체
     */
    @CacheEvict(value = {"postCache"}, allEntries = true)
    public void savePostReaction(PostReaction newPostReaction) {
        Member member = getMember(newPostReaction.getMemberId());

        if (!isExistPost(newPostReaction.getPostIdx())) {
            throw new InvalidPostException(ErrorType.NOT_FOUND_POST);
        }

        PostReactionResponse existPostReaction = getPostReaction(newPostReaction.getPostIdx(),
                newPostReaction.updateMemberIdx(member.getMemberIdx()).getMemberIdx());

        if (existPostReaction.getReaction() == Reaction.NOTTING) {
            handleNonExistingPostReaction(newPostReaction);
        }
        if (existPostReaction.getReaction() != Reaction.NOTTING) {
            handleExistingPostReaction(newPostReaction, existPostReaction);
        }
    }

    /**
     * 게시글 번호에 해당하는 게시글의 조회수를 증가
     *
     * @param postIdx 게시글 번호
     */
    public void incrementViewCount(Long postIdx) {
        postRepository.incrementViewCount(postIdx);
    }


    /**
     * 게시글에 대한 회원의 반응 정보를 조회 후 반환
     *
     * @param postIdx   게시글 번호
     * @param memberIdx 회원 번호
     * @return 반응 정보를 포함한 {@link PostReactionResponse} 객체
     */
    private PostReactionResponse getPostReaction(Long postIdx, Long memberIdx) {
        PostReactionResponse postReactionResponse = postRepository.getPostReaction(PostReaction.create(postIdx, memberIdx));
        if (postReactionResponse == null) {
            return new PostReactionResponse().createNoReactionPostReaction();
        }
        return postReactionResponse;
    }


    /**
     * 게시글에 대한 회원의 반응 및 상세 정보를 조회 후 반환
     *
     * @param postIdx   게시글 번호
     * @param memberIdx 회원 번호
     * @return 반응 정보와 상세 정보를 포함한 {@link PostReactionResponse} 객체
     */
    private PostReactionResponse getPostReactionDetails(Long postIdx, Long memberIdx) {
        PostReactionResponse postReactionResponse = postRepository.getPostReactionDetails(PostReaction.create(postIdx, memberIdx));
        if (postReactionResponse.getReaction() == null) {
            return postReactionResponse.createNoReactionPostReaction();
        }
        return postReactionResponse;
    }


    /**
     * 기존 반응 정보를 처리
     *
     * @param newReaction      새로운 반응 정보
     * @param existingReaction 기존 반응 정보
     */
    private void handleExistingPostReaction(PostReaction newReaction, PostReactionResponse existingReaction) {
        if (!existingReaction.getReaction().canUpdate(newReaction.getReaction())) {
            throw new CannotUpdateReactionException(ErrorType.CAN_NOT_UPDATE_REACTION);
        }
        adjustExistReactionCount(existingReaction, newReaction.getPostIdx());
        adjustNewReactionCount(newReaction);
        postRepository.updatePostReaction(newReaction);
    }


    /**
     * 새로운 반응 정보를 처리
     *
     * @param newReaction 새로운 반응 정보
     */
    private void handleNonExistingPostReaction(PostReaction newReaction) {
        if (!newReaction.canSave()) {
            throw new CannotUpdateReactionException(ErrorType.CAN_NOT_UPDATE_REACTION);
        }
        adjustNewReactionCount(newReaction);
        postRepository.savePostReaction(newReaction);
    }

    /**
     * 기존 반응에 따라 게시글의 카운트를 조정
     *
     * @param existingReaction 반응 정보를 포함하는 객체
     */
    private void adjustExistReactionCount(PostReactionResponse existingReaction, Long postIdx) {
        if (existingReaction.getReaction() == Reaction.LIKE) {
            postRepository.decrementLikeCount(postIdx);
        }
        if (existingReaction.getReaction() == Reaction.DISLIKE) {
            postRepository.decrementDislikeCount(postIdx);
        }
    }

    /**
     * 새로운 반응에 따라 게시글의 카운트를 조정
     *
     * @param newReaction 반응 정보를 포함하는 객체
     */
    private void adjustNewReactionCount(PostReaction newReaction) {
        if (newReaction.getReaction() == Reaction.LIKE) {
            postRepository.incrementLikeCount(newReaction.getPostIdx());
        }
        if (newReaction.getReaction() == Reaction.DISLIKE) {
            postRepository.incrementDislikeCount(newReaction.getPostIdx());
        }
    }


    /**
     * 게시글 번호에 해당하는 게시물이 있다면 true 반환, 그렇지 않다면 flase 반환
     *
     * @param postIdx 게시글 번호
     * @return 게시글 번호에 해당하는 게시물이 있다면 true 반환, 그렇지 않다면 flase 반환
     */
    private boolean isExistPost(Long postIdx) {
        return postRepository.existPostByPostId(postIdx);
    }


    /**
     * 회원 정보 응답
     *
     * @param memberId 회원 아이디
     * @return 회원 도메인 객체
     */
    private Member getMember(String memberId) {
        Member member = memberRepository.findById(memberId);
        if (member == null) {
            throw new InvalidMemberException(ErrorType.NOT_FOUND_MEMBER);
        }
        return member;
    }

    /**
     * 게시글 번호에 해당하는 게시글 반응 삭제
     *
     * @param postIdx 게시글 번호
     */
    public void deleteAllPostReactionByPostIdx(Long postIdx) {
        postRepository.deleteAllPostReactionByPostIdx(postIdx);
    }
}

