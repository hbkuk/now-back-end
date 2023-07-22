package com.now.core.comment.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
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
public class Comment {

    private final Long commentIdx; // 댓글의 고유 식별자

    private String memberNickname; // 댓글의 작성자의 닉네임

    @Size(max = 2000)
    private final String content; // 댓글의 내용

    private final Long postIdx; // 게시글의 고유 식별자

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime regDate; // 댓글의 등록일자

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long memberIdx; // 댓글의 작성자의 고유 식별자

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String memberId; // 댓글의 작성자의 아이디

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
     * 댓글을 수정할 수 있다면 true 반환, 그렇지 않다면 false 반환
     *
     * @param member 회원 정보가 담긴 객체
     * @return 댓글을 수정할 수 있다면 true 반환, 그렇지 않다면 false 반환
     */
    public boolean canUpdate(Member member) {
        return member.isSameMemberId(this.memberId);
    }
}
