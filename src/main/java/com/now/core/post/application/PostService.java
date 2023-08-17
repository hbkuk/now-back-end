package com.now.core.post.application;

import com.now.common.exception.ErrorType;
import com.now.core.member.domain.Member;
import com.now.core.member.domain.MemberRepository;
import com.now.core.member.exception.InvalidMemberException;
import com.now.core.post.domain.repository.PostRepository;
import com.now.core.post.exception.CannotUpdateReactionException;
import com.now.core.post.exception.InvalidPostException;
import com.now.core.post.presentation.dto.Condition;
import com.now.core.post.presentation.dto.PostReaction;
import com.now.core.post.presentation.dto.Posts;
import com.now.core.post.presentation.dto.constants.Reaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
     * 게시글 번호에 해당하는 게시글의 조회수를 증가
     *
     * @param postIdx 게시글 번호
     */
    public void incrementViewCount(Long postIdx) {
        postRepository.incrementViewCount(postIdx);
    }

    /**
     * 반응 정보 업데이트
     *
     * @param newPostReaction 업데이트할 반응 정보를 포함하는 객체
     */
    public void updatePostReaction(PostReaction newPostReaction) {
        Member member = getMember(newPostReaction.getMemberId());

        if (!isExistPost(newPostReaction.getPostIdx())) {
            throw new InvalidPostException(ErrorType.NOT_FOUND_POST);
        }

        PostReaction exisitPostReaction = postRepository
                .getPostReaction(newPostReaction.updateMemberIdx(member.getMemberIdx()));

        if (exisitPostReaction != null) {
            handleExistingPostReaction(newPostReaction, exisitPostReaction);
        }

        if (exisitPostReaction == null) {
            handleNonExistingPostReaction(newPostReaction);
        }
    }


    /**
     * 기존 반응 정보를 처리
     *
     * @param newReaction      새로운 반응 정보
     * @param existingReaction 기존 반응 정보
     */
    private void handleExistingPostReaction(PostReaction newReaction, PostReaction existingReaction) {
        if (!existingReaction.canUpdate(newReaction)) {
            throw new CannotUpdateReactionException(ErrorType.CAN_NOT_UPDATE_REACTION);
        }
        adjustExistReactionCount(existingReaction);
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
    private void adjustExistReactionCount(PostReaction existingReaction) {
        if (existingReaction.getReaction() == Reaction.LIKE) {
            postRepository.decrementLikeCount(existingReaction.getPostIdx());
        }
        if (existingReaction.getReaction() == Reaction.UNLIKE) {
            postRepository.incrementLikeCount(existingReaction.getPostIdx());
        }
        if (existingReaction.getReaction() == Reaction.DISLIKE) {
            postRepository.decrementDislikeCount(existingReaction.getPostIdx());
        }
        if (existingReaction.getReaction() == Reaction.UNDISLIKE) {
            postRepository.incrementDislikeCount(existingReaction.getPostIdx());
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
        if (newReaction.getReaction() == Reaction.UNLIKE) {
            postRepository.decrementLikeCount(newReaction.getPostIdx());
        }
        if (newReaction.getReaction() == Reaction.DISLIKE) {
            postRepository.incrementDislikeCount(newReaction.getPostIdx());
        }
        if (newReaction.getReaction() == Reaction.UNDISLIKE) {
            postRepository.decrementDislikeCount(newReaction.getPostIdx());
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
}

