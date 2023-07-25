package com.now.core.comment.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 댓글 관련 정보를 관리하는 레포지토리
 */
@Repository
@RequiredArgsConstructor
public class CommentRepository {

    private final CommentMapper commentMapper;

    /**
     * 게시글 번호에 해당하는 댓글 정보 조회 후 반환
     *
     * @param postIdx 게시글 번호
     * @return 댓글 정보 리스트
     */
    public List<Comment> findAllByPostIdx(Long postIdx) {
        return commentMapper.findAllByPostIdx(postIdx);
    }

    /**
     * 댓글 정보 응답
     *
     * @param commentIdx 댓글 번호
     * @return 댓글 도메인 객체
     */
    public Comment findComment(Long commentIdx) {
        return commentMapper.findComment(commentIdx);
    }

    /**
     * 댓글 등록
     *
     * @param comment 등록할 댓글 정보
     */
    public void saveCommentByMember(Comment comment) {
        commentMapper.saveCommentByMember(comment);
    }

    /**
     * 댓글 수정
     *
     * @param comment 수정할 댓글 정보
     */
    public void updateComment(Comment comment) {
        commentMapper.updateComment(comment);
    }

    /**
     * 게시글 번호에 해당하는 모든 댓글 삭제
     *
     * @param postIdx 게시글 번호
     */
    public void deleteAllByPostIdx(Long postIdx) {
        commentMapper.deleteAllByPostIdx(postIdx);
    }

    /**
     * 댓글 번호에 해당하는 댓글 삭제
     * 
     * @param commentIdx 댓글 번호
     */
    public void deleteComment(Long commentIdx) {
        commentMapper.deleteComment(commentIdx);
    }
}
