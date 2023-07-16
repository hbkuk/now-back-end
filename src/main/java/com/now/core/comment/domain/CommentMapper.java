package com.now.core.comment.domain;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper {

    /**
     * 게시글 번호에 해당하는 댓글 정보 조회 후 반환
     *
     * @param postIdx 게시글 번호
     * @return 댓글 정보 리스트
     */
    List<Comment> findAllByPostIdx(Long postIdx);

    /**
     * 게시글 번호에 해당하는 모든 댓글 삭제
     *
     * @param postIdx 게시글 번호
     */
    void deleteAllByPostIdx(Long postIdx);

}
