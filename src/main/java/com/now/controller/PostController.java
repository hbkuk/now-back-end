package com.now.controller;

import com.now.domain.post.Community;
import com.now.service.PostService;
import com.now.validation.PostValidationGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 게시글 관련 작업을 위한 컨트롤러
 */
@Slf4j
@RestController
public class PostController {

    private final PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }


    /**
     * 모든 게시글 정보를 조회하는 핸들러 메서드
     *
     * @param response 게시글 정보를 담을 Map 객체
     * @return 모든 게시글 정보와 함께 OK 응답을 반환
     */
    //TODO: 일급컬렉션으로 리팩토링
    @GetMapping("/api/post")
    public ResponseEntity<Map<String, Object>> retrieveAllPosts(Map<String, Object> response) {
        log.debug("getAllPosts 호출");

        response.put("notices", postService.retrieveAllNotices());
        response.put("community", postService.retrieveAllCommunity());
        response.put("photos", postService.retrieveAllPhotos());
        response.put("inquiries", postService.retrieveAllInquiries());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * 커뮤니티 게시글을 등록
     *
     * @param userId    작성자의 사용자 ID
     * @param community 등록할 커뮤니티 게시글 정보
     * @return 생성된 게시글에 대한 CREATED 응답을 반환
     */
    @PostMapping("/api/community")
    public ResponseEntity<Object> registerCommunityPost(@RequestAttribute("userId") String userId,
                                                        @RequestBody @Validated(PostValidationGroup.register.class) Community community) {
        log.debug("registerCommunityPost 호출");

        postService.registerPost(community.updateAuthorId(userId));

        return ResponseEntity.status(HttpStatus.CREATED).build(); // Status Code 201
    }

}
