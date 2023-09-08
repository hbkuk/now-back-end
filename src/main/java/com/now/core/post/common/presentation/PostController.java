package com.now.core.post.common.presentation;

import com.now.core.authentication.application.JwtTokenProvider;
import com.now.core.authentication.presentation.AuthenticationPrincipal;
import com.now.core.post.common.application.PostService;
import com.now.core.post.common.domain.constants.PostValidationGroup;
import com.now.core.post.common.presentation.dto.Condition;
import com.now.core.post.common.presentation.dto.PostReaction;
import com.now.core.post.common.presentation.dto.PostReactionResponse;
import com.now.core.post.common.presentation.dto.PostsResponse;
import com.now.core.post.common.presentation.dto.constants.PostReactionValidationGroup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final JwtTokenProvider jwtTokenProvider;

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
     * 게시글 반응 정보를 조회 후 반환
     *
     * @param postIdx 게시글 번호
     * @param isReactionDetails 반응에 대한 상세 정보 반환 여부
     * @param accessToken 액세스 토큰
     * @return 게시글 반응 정보와 함께 OK 응답을 반환
     */
    @GetMapping("/api/posts/{postIdx}/reaction")
    public ResponseEntity<PostReactionResponse> getPostReaction(@PathVariable("postIdx") Long postIdx,
                                                                @RequestParam(name = "isReactionDetails", required = false, defaultValue = "false") boolean isReactionDetails,
                                                                @CookieValue(value = JwtTokenProvider.ACCESS_TOKEN_KEY, required = true) String accessToken) {
        return new ResponseEntity<>(postService.getPostReaction(postIdx, (String) jwtTokenProvider.getClaim(accessToken, "id"), isReactionDetails), HttpStatus.OK);
    }

    /**
     * 게시글 반응 저장
     *
     * @param memberId     회원 아이디
     * @param postReaction 게시물 반응 객체
     * @return CREATED 응답 반환
     */
    @PostMapping("/api/posts/{postIdx}/reaction")
    public ResponseEntity<Void> savePostReaction(@PathVariable("postIdx") Long postIdx,
                                                 @AuthenticationPrincipal String memberId,
                                                 @RequestBody @Validated(PostReactionValidationGroup.savePostReaction.class) PostReaction postReaction) {
        postService.savePostReaction(postReaction.updatePostIdx(postIdx).updateMemberId(memberId));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
