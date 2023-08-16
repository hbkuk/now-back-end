package com.now.core.post.domain.repository;

import com.now.core.post.domain.mapper.PostMapper;
import com.now.core.post.presentation.dto.Condition;
import com.now.core.post.presentation.dto.Posts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 게시글 관련 정보를 관리하는 레포지토리
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class PostRepository {

    private final PostMapper postMapper;

    /**
     * 모든 게시글 정보를 조회 후 반환
     *
     * @param condition 게시물 제한 정보를 담은 객체
     * @return 게시글 정보 리스트
     */
    public List<Posts> findAllPosts(Condition condition) {
        return postMapper.findAllPosts(condition);
    }

    /**
     * 조건에 맞는 게시물을 조회 후 수량 반환
     *
     * @param condition 조건 객체
     * @return 조건에 맞는 게시물을 조회 후 수량 반환
     */
    public Long findTotalPostCount(Condition condition) {
        return postMapper.findTotalPostCount(condition);
    }


    /**
     * 게시글 번호에 해당하는 게시글이 있다면 true 반환, 그렇지 않다면 false 반환
     *
     * @param postIdx 게시글 번호
     * @return 게시글이 있다면 true 반환, 그렇지 않다면 false 반환
     */
    public boolean existPostByPostId(Long postIdx) {
        return postMapper.existPostByPostId(postIdx);
    }
}
