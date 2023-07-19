package com.now.core.comment.application;

import com.now.core.comment.domain.Comment;
import com.now.core.comment.domain.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 댓글 관련 비즈니스 로직을 처리하는 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    /**
     * 게시글 번호에 해당하는 댓글 정보 조회 후 반환
     *
     * @param postIdx 게시글 번호
     * @return 댓글 정보 리스트
     */
    public List<Comment> getAllByPostIdx(Long postIdx) {
        return commentRepository.findAllByPostIdx(postIdx);
    }

    /**
     * 게시글 번호에 해당하는 모든 댓글 삭제
     *
     * @param postIdx 게시글 번호
     */
    public void deleteAllByPostIdx(Long postIdx) {
        commentRepository.deleteAllByPostIdx(postIdx);
    }
}
