package com.now.core.post.application;

import com.now.core.post.domain.PostRepository;
import com.now.core.post.presentation.dto.Condition;
import com.now.core.post.presentation.dto.Posts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 커뮤니티 게시글 관련 비즈니스 로직을 처리하는 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor

public class PostService {

    private final PostRepository postRepository;

    /**
     * 모든 커뮤니티 게시글 정보를 조회 후 반환
     *
     * @return 커뮤니티 게시글 정보 리스트
     */
    @Cacheable(value = "postCache", key="#condition.maxNumberOfPosts")
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
    @Cacheable(value = "postCache", key="#condition.hashCode()")
    public Long getTotalPostCount(Condition condition) {
        return postRepository.findTotalPostCount(condition);
    }

}

