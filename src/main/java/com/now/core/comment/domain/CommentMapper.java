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
     * 댓글 정보 응답
     *
     * @param commentIdx 댓글 번호
     * @return 댓글 도메인 객체
     */
    Comment findComment(Long commentIdx);


    /**
     * 댓글 등록
     *
     * @param comment 등록할 댓글 정보
     */
    void saveCommentByMember(Comment comment);

    void saveCommentByManager(Comment comment);



    /**
     * 댓글 수정
     *
     * @param comment 수정할 댓글 정보
     */
    void updateComment(Comment comment);




    /**
     * 게시글 번호에 해당하는 모든 댓글 삭제
     *
     * @param postIdx 게시글 번호
     */
    void deleteAllByPostIdx(Long postIdx);

    /**
     * 댓글 번호에 해당하는 댓글 삭제
     *
     * @param commentIdx 댓글 번호
     */
    void deleteComment(Long commentIdx);
}
