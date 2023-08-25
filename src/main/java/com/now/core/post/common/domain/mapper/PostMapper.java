package com.now.core.post.common.domain.mapper;

import com.now.core.post.common.presentation.dto.Condition;
import com.now.core.post.common.presentation.dto.PostReaction;
import com.now.core.post.common.presentation.dto.PostReactionResponse;
import com.now.core.post.common.presentation.dto.Posts;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 게시글 정보에 접근하는 매퍼 인터페이스
 */
@Mapper
public interface PostMapper {

    /**
     * 모든 게시글 정보를 조회 후 반환
     *
     * @param condition 게시물 제한 정보를 담은 객체
     * @return 게시글 정보 리스트
     */
    List<Posts> findAllPosts(Condition condition);


    /**
     * 조건에 맞는 게시물을 조회 후 수량 반환
     *
     * @param condition 조건 객체
     * @return 조건에 맞는 게시물을 조회 후 수량 반환
     */
    Long findTotalPostCount(Condition condition);


    /**
     * 게시글 번호에 해당하는 게시글의 조회수를 증가
     *
     * @param postIdx 게시글 번호
     */
    void incrementViewCount(Long postIdx);


    /**
     * 게시글 번호에 해당하는 게시글의 좋아요 증가
     *
     * @param postIdx 게시글 번호
     */
    void incrementLikeCount(Long postIdx);


    /**
     * 게시글 번호에 해당하는 게시글의 좋아요 감소
     *
     * @param postIdx 게시글 번호
     */
    void decrementLikeCount(Long postIdx);


    /**
     * 게시글 번호에 해당하는 게시글의 싫어요 증가
     *
     * @param postIdx 게시글 번호
     */
    void incrementDislikeCount(Long postIdx);


    /**
     * 게시글 번호에 해당하는 게시글의 싫어요 감소
     *
     * @param postIdx 게시글 번호
     */
    void decrementDislikeCount(Long postIdx);


    /**
     * 게시글 번호에 해당하는 게시글의 반응 정보만 조회 후 반환
     *
     * @param postReaction 게시글 반응 정보
     * @return 게시글의 반응 정보만 조회 후 반환
     */
    PostReactionResponse getPostReaction(PostReaction postReaction);


    /**
     * 게시글 번호에 해당하는 게시글의 반응 정보와 좋아요, 싫어요 조회 후 반환
     *
     * @param postReaction 게시글 반응 정보
     * @return 게시글의 반응 정보와 좋아요, 싫어요 조회 후 반환
     */
    PostReactionResponse getPostReactionDetails(PostReaction postReaction);


    /**
     * 게시글 번호에 해당하는 게시글의 반응 저장
     *
     * @param postReaction 리액션 정보
     */
    void savePostReaction(PostReaction postReaction);


    /**
     * 게시글 번호에 해당하는 게시글의 반응 수정
     *
     * @param postReaction 리액션 정보
     */
    void updatePostReaction(PostReaction postReaction);


    /**
     * 게시글 번호에 해당하는 게시글 반응 삭제
     * 
     * @param postIdx 게시글 번호
     */
    void deleteAllPostReactionByPostIdx(Long postIdx);


    /**
     * 주어진 게시물 번호에 해당하는 게시물이 존재한다면 true, 그렇지 않다면 false 반환
     *
     * @param postIdx 게시글 번호
     * @return 주어진 게시물 번호에 해당하는 게시물이 존재한다면 true, 그렇지 않다면 false 반환
     */
    boolean existPostByPostId(Long postIdx);
}
