package com.now.core.comment.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.now.core.member.domain.Member;
import lombok.*;

import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * 댓글 정보를 담고있는 도메인 객체
 */
@Builder(toBuilder = true)
@ToString
@Getter
@NoArgsConstructor(force = true)
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Comment {

    //TODO: 댓글 `like_count`, `dislike_count` 기능 구현

    private Long postIdx; // 게시글의 고유 식별자

    private Long commentIdx; // 댓글의 고유 식별자

    private String memberNickname; // 회원 닉네임

    private String managerNickname; // 매니저 닉네임

    @Size(groups = CommentValidationGroup.saveComment.class, min = 1, max = 2000, message = "{comment.content.size}")
    private final String content; // 댓글의 내용

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime regDate; // 댓글의 등록일자

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long memberIdx; // 회원 고유 식별자

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String memberId; // 회원 아이디

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long managerIdx; // 매니저 고유 식별자

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String managerId; // 매니저 아이디


    /**
     * 댓글을 수정할 수 있다면 true 반환, 그렇지 않다면 false 반환
     *
     * @param member 회원 정보가 담긴 객체
     * @return 댓글을 수정할 수 있다면 true 반환, 그렇지 않다면 false 반환
     */
    public boolean canUpdate(Member member) {
        return member.isSameMemberId(this.memberId);
    }

    /**
     * 댓글을 삭제할 수 있다면 true 반환, 그렇지 않다면 false 반환
     *
     * @param member 회원 정보가 담긴 객체
     * @return 댓글을 삭제할 수 있다면 true 반환, 그렇지 않다면 false 반환
     */
    public boolean canDelete(Member member) {
        return member.isSameMemberId(this.memberId);
    }

    /**
     * 게시글 번호 업데이트
     *
     * @param postIdx 게시글 번호
     * @return 업데이트된 Comment 객체
     */
    public Comment updatePostIdx(Long postIdx) {
        this.postIdx = postIdx;
        return this;
    }
    
    /**
     * 회원 아이디 업데이트
     *
     * @param memberId 회원 아이디
     * @return 업데이트된 Comment 객체
     */
    public Comment updateMemberId(String memberId) {
        this.memberId = memberId;
        return this;
    }

    /**
     * 회원 번호 업데이트
     *
     * @param memberIdx 회원 번호
     * @return 업데이트된 Comment 객체
     */
    public Comment updateMemberIdx(Long memberIdx) {
        this.memberIdx = memberIdx;
        return this;
    }

    /**
     * 댓글 번호 업데이트
     *
     * @param commentIdx 댓글 번호
     * @return 업데이트된 Comment 객체
     */
    public Comment updateCommentIdx(Long commentIdx) {
        this.commentIdx = commentIdx;
        return this;
    }
}
