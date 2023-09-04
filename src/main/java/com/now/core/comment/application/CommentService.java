package com.now.core.comment.application;

import com.now.common.exception.ErrorType;
import com.now.core.comment.domain.Comment;
import com.now.core.comment.domain.CommentRepository;
import com.now.core.comment.exception.CannotDeleteCommentException;
import com.now.core.comment.exception.CannotUpdateCommentException;
import com.now.core.comment.exception.InvalidCommentException;
import com.now.core.comment.presentation.dto.CommentsResponse;
import com.now.core.member.domain.Member;
import com.now.core.member.domain.MemberRepository;
import com.now.core.member.exception.InvalidMemberException;
import com.now.core.post.common.domain.repository.PostRepository;
import com.now.core.post.common.exception.InvalidPostException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.now.common.config.CachingConfig.*;

/**
 * 댓글 관련 비즈니스 로직을 처리하는 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final CommentRepository commentRepository;

    /**
     * 게시글 번호에 해당하는 모든 댓글 정보를 조회
     *
     * @param postIdx 게시글 번호
     * @return 모든 댓글 정보와 함께 OK 응답을 반환
     */
    @Transactional(readOnly = true)
    public CommentsResponse getAllComments(Long postIdx) {
        if (!isExistPost(postIdx)) {
            throw new InvalidPostException(ErrorType.NOT_FOUND_POST);
        }
        return CommentsResponse.builder()
                .comments(commentRepository.findAllByPostIdx(postIdx))
                .build();
    }

    /**
     * 댓글 등록
     *
     * @param comment 등록할 댓글 정보
     */
    @CacheEvict(value = {POST_CACHE, NOTICE_CACHE, COMMUNITY_CACHE, PHOTO_CACHE, INQUIRY_CACHE}, allEntries = true)
    public void registerCommentByMember(Comment comment) {
        Member member = getMember(comment.getMemberId());

        if (!isExistPost(comment.getPostIdx())) {
            throw new InvalidPostException(ErrorType.NOT_FOUND_POST);
        }

        commentRepository.saveCommentByMember(comment.updateMemberIdx(member.getMemberIdx()));
    }

    /**
     * 댓글 수정
     *
     * @param updatedComment 수정할 댓글 정보
     */
    @CacheEvict(value = {POST_CACHE, NOTICE_CACHE, COMMUNITY_CACHE, PHOTO_CACHE, INQUIRY_CACHE}, allEntries = true)
    public void updateCommentByMember(Comment updatedComment) {
        Member member = getMember(updatedComment.getMemberId());

        if (!isExistPost(updatedComment.getPostIdx())) {
            throw new InvalidPostException(ErrorType.NOT_FOUND_POST);
        }

        Comment comment = getComment(updatedComment.getCommentIdx());
        if(!comment.canUpdate(member)) {
            throw new CannotUpdateCommentException(ErrorType.CAN_NOT_UPDATE_OTHER_MEMBER_COMMENT);
        }

        commentRepository.updateComment(updatedComment.updateMemberIdx(member.getMemberIdx()));
    }

    /**
     * 댓글 삭제
     *
     * @param postIdx    게시글 번호
     * @param commentIdx 댓글 번호
     * @param memberId   회원 아이디
     */
    @CacheEvict(value = {POST_CACHE, NOTICE_CACHE, COMMUNITY_CACHE, PHOTO_CACHE, INQUIRY_CACHE}, allEntries = true)
    public void deleteCommentByMember(Long postIdx, Long commentIdx, String memberId) {
        Member member = getMember(memberId);

        if (!isExistPost(postIdx)) {
            throw new InvalidPostException(ErrorType.NOT_FOUND_POST);
        }

        Comment comment = getComment(commentIdx);
        if(!comment.canDelete(member)) {
            throw new CannotDeleteCommentException(ErrorType.CAN_NOT_DELETE_OTHER_MEMBER_COMMENT);
        }

        commentRepository.deleteComment(commentIdx);
    }

    /**
     * 게시글 번호에 해당하는 모든 댓글 삭제
     *
     * @param postIdx 게시글 번호
     */
    public void deleteAllByPostIdx(Long postIdx) {
        commentRepository.deleteAllByPostIdx(postIdx);
    }

    /**
     * 게시글 번호에 해당하는 게시물이 있다면 true 반환, 그렇지 않다면 flase 반환
     * 
     * @param postIdx 게시글 번호
     * @return 게시글 번호에 해당하는 게시물이 있다면 true 반환, 그렇지 않다면 flase 반환
     */
    private boolean isExistPost(Long postIdx) {
        return postRepository.existPostByPostId(postIdx);
    }

    /**
     * 회원 정보 응답
     *
     * @param memberId 회원 아이디
     * @return 회원 도메인 객체
     */
    private Member getMember(String memberId) {
        Member member = memberRepository.findById(memberId);
        if (member == null) {
            throw new InvalidMemberException(ErrorType.NOT_FOUND_MEMBER);
        }
        return member;
    }

    /**
     * 댓글 정보 응답
     * 
     * @param commentIdx 댓글 번호
     * @return 댓글 도메인 객체
     */
    private Comment getComment(Long commentIdx) {
        Comment comment = commentRepository.findComment(commentIdx);
        if (comment == null) {
            throw new InvalidCommentException(ErrorType.NOT_FOUND_COMMENT);
        }
        return comment;
    }
}
