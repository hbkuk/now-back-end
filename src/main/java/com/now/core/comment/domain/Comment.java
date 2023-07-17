package com.now.core.comment.domain;

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
public class Comment {

    /**
     * 댓글의 고유 식별자
     */
    private final Long commentIdx;

    /**
     * 게시글을 작성자한 회원의 고유 식별자
     */
    private final String memberIdx;

    /**
     * 댓글의 등록일자
     */
    private final LocalDateTime regDate;

    /**
     * 댓글의 내용
     */
    @Size(max = 2000)
    private final String content;

    /**
     * 게시글의 고유 식별자
     */
    private final Long memberPostIdx;

    /**
     * 댓글을 삭제할 수 있다면 true 반환, 그렇지 않다면 false 반환
     *
     * @param member 회원 정보가 담긴 객체
     * @return 댓글을 삭제할 수 있다면 true 반환, 그렇지 않다면 false 반환
     */
    public boolean canDelete(Member member) {
        return member.isSameMemberId(this.memberIdx);
    }

    /**
     * 댓글을 수정할 수 있다면 true 반환, 그렇지 않다면 false 반환
     *
     * @param member 회원 정보가 담긴 객체
     * @return 댓글을 수정할 수 있다면 true 반환, 그렇지 않다면 false 반환
     */
    public boolean canUpdate(Member member) {
        return member.isSameMemberId(this.memberIdx);
    }
}
