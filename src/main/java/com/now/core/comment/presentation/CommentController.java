package com.now.core.comment.presentation;

import com.now.core.authentication.presentation.AuthenticationPrincipal;
import com.now.core.comment.application.CommentService;
import com.now.core.comment.domain.Comment;
import com.now.core.comment.domain.CommentValidationGroup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 댓글 관련 작업을 위한 컨트롤러
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    /**
     * 댓글 등록
     *
     * @param postIdx  원글 번호
     * @param memberId 회원 ID
     * @param comment  댓글 정보
     * @return 생성된 댓글에 대한 CREATED 응답을 반환
     */
    @PostMapping("/api/posts/{postIdx}/comments")
    public ResponseEntity<Void> registerComment(@PathVariable("postIdx") Long postIdx,
                                                @AuthenticationPrincipal String memberId,
                                                @RequestBody @Validated({CommentValidationGroup.saveComment.class}) Comment comment) {
        commentService.registerCommentByMember(comment.updatePostIdx(postIdx).updateMemberId(memberId));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 댓글 수정
     *
     * @param postIdx  원글 번호
     * @param memberId 회원 ID
     * @param comment  댓글 정보
     * @return 생성된 댓글에 대한 CREATED 응답을 반환
     */
    @PutMapping("/api/posts/{postIdx}/comments/{commentIdx}")
    public ResponseEntity<Void> updateComment(@PathVariable("postIdx") Long postIdx,
                                              @PathVariable("commentIdx") Long commentIdx,
                                              @AuthenticationPrincipal String memberId,
                                              @RequestBody @Validated({CommentValidationGroup.saveComment.class}) Comment comment) {
        commentService.updateCommentByMember(comment.updatePostIdx(postIdx).updateMemberId(memberId).updateCommentIdx(commentIdx));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 댓글 삭제
     *
     * @param postIdx    원글 번호
     * @param commentIdx 댓글 번호
     * @param memberId   회원 ID
     * @return 응답 본문이 없는 상태 코드 204 반환
     */
    @DeleteMapping("/api/posts/{postIdx}/comments/{commentIdx}")
    public ResponseEntity<Void> deleteComment(@PathVariable("postIdx") Long postIdx,
                                              @PathVariable("commentIdx") Long commentIdx,
                                              @AuthenticationPrincipal String memberId) {
        commentService.deleteCommentByMember(postIdx, commentIdx, memberId);
        return ResponseEntity.noContent().build();
    }
}
