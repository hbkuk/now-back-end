package com.now.core.post.domain.mapper;

import com.now.core.post.presentation.dto.Condition;
import com.now.core.post.presentation.dto.Posts;
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
     * 주어진 게시물 번호에 해당하는 게시물이 존재한다면 true, 그렇지 않다면 false 반환
     *
     * @param postIdx 게시글 번호
     * @return 주어진 게시물 번호에 해당하는 게시물이 존재한다면 true, 그렇지 않다면 false 반환
     */
    boolean existPostByPostId(Long postIdx);

}
