package com.now.core.post.presentation;

import com.now.core.post.application.PostService;
import com.now.core.post.presentation.dto.Condition;
import com.now.core.post.presentation.dto.PostsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    /**
     * 모든 게시글 정보를 조회 후 반환
     *
     * @return 모든 게시글 정보와 함께 OK 응답을 반환
     */
    @GetMapping("/api/posts")
    public ResponseEntity<PostsResponse> getAllPosts(@Valid @ModelAttribute("Condition")Condition condition) {
        log.debug("getAllPosts 호출");

        return new ResponseEntity<>(PostsResponse.convertToPostsResponse(postService.getAllPosts(condition)), HttpStatus.OK);
    }
}
