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
     * 게시글 번호에 해당하는 모든 댓글 삭제
     *
     * @param postIdx 게시글 번호
     */
    public void deleteAllByPostIdx(Long postIdx) {
        commentMapper.deleteAllByPostIdx(postIdx);
    }
}
