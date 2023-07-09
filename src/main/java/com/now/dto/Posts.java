package com.now.dto;

import com.now.domain.post.Post;
import lombok.Getter;

import java.util.List;
import java.util.Map;

/**
 * Post를 상속받는 게시글을 표현하는 포장 객체
 */
@Getter
public class Posts {
    private final Map<String, List<? extends Post>> posts;

    private Posts(Map<String, List<? extends Post>> posts) {
        this.posts = posts;
    }

    /**
     * 새로운 Posts 인스턴스를 생성 후 반환
     *
     * @param posts 게시글의 리스트를 포함하는 맵. 키는 게시글의 유형, 값은 해당 유형의 게시글 목록
     * @return 새로운 Posts 인스턴스
     */
    public static Posts create(Map<String, List<? extends Post>> posts) {
        return new Posts(posts);
    }
}
