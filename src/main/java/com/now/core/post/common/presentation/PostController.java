package com.now.core.post.common.presentation;

import com.now.core.authentication.presentation.AuthenticationPrincipal;
import com.now.core.post.common.application.PostService;
import com.now.core.post.common.domain.constants.PostValidationGroup;
import com.now.core.post.common.presentation.dto.Condition;
import com.now.core.post.common.presentation.dto.PostsResponse;
import com.now.core.post.common.presentation.dto.PostReaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
    public ResponseEntity<PostsResponse> getAllPosts(@Validated(PostValidationGroup.getAllPosts.class) Condition condition) {
        return new ResponseEntity<>(PostsResponse.convertToPostsResponse(postService.getAllPosts(condition)), HttpStatus.OK);
    }

    /**
     * 게시글 반응 저장
     *
     * @param memberId 회원 아이디
     * @param postReaction 게시물 반응 객체
     * @return CREATED 응답 반환
     */
    @PostMapping("/posts/{postIdx}/reaction")
    public ResponseEntity<Void> updateReaction(@PathVariable("postIdx") Long postIdx,
                                               @AuthenticationPrincipal String memberId,
                                               @Valid PostReaction postReaction) {

        postService.updatePostReaction(postReaction.updatePostIdx(postIdx).updateMemberId(memberId));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
