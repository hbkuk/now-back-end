package com.now.controller;

import com.now.domain.post.Community;
import com.now.domain.post.Notice;
import com.now.dto.Posts;
import com.now.security.Authority;
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
     * @return 모든 게시글 정보와 함께 OK 응답을 반환
     */
    @GetMapping("/api/post")
    public ResponseEntity<Posts> retrievePosts() {
        log.debug("retrievePosts 호출");

        Posts posts = Posts.create(Map.of(
                "notices", postService.retrieveAllNotices(),
                "community", postService.retrieveAllCommunity(),
                "photos", postService.retrieveAllPhotos(),
                "inquiries", postService.retrieveAllInquiries()
        ));

        return new ResponseEntity<>(posts, HttpStatus.OK);
    }

    /**
     * 공지 게시글을 등록
     *
     * @param managerId 작성자의 관리자 ID
     * @param notice    등록할 공지 게시글 정보
     * @return 생성된 게시글에 대한 CREATED 응답을 반환
     */
    @PostMapping("/api/notice")
    public ResponseEntity<Void> registerNoticePost(@RequestAttribute("id") String managerId,
                                                     @RequestAttribute("role") String authority,
                                                     @RequestBody @Validated(PostValidationGroup.register.class) Notice notice) {
        log.debug("registerNoticePost 호출, managerId : {}, authority : {}", managerId, authority);

        postService.registerNoticePost(notice.updateAuthorId(managerId), Authority.valueOf(authority));

        return ResponseEntity.status(HttpStatus.CREATED).build(); // Status Code 201
    }

    /**
     * 커뮤니티 게시글을 등록
     *
     * @param userId    작성자의 사용자 ID
     * @param community 등록할 커뮤니티 게시글 정보
     * @return 생성된 게시글에 대한 CREATED 응답을 반환
     */
    @PostMapping("/api/community")
    public ResponseEntity<Void> registerCommunityPost(@RequestAttribute("id") String userId,
                                                        @RequestAttribute("role") String authority,
                                                        @RequestBody @Validated(PostValidationGroup.register.class) Community community) {
        log.debug("registerCommunityPost 호출, userId : {}, authority : {}", userId, authority);

        postService.registerCommunityPost(community.updateAuthorId(userId), Authority.valueOf(authority));

        return ResponseEntity.status(HttpStatus.CREATED).build(); // Status Code 201
    }

}
