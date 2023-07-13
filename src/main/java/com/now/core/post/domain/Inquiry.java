package com.now.core.post.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.now.core.category.domain.constants.PostGroup;
import com.now.core.post.domain.abstractions.MemberPost;
import com.now.core.post.exception.CannotViewInquiryException;
import com.now.core.member.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * 문의 게시글을 나타내는 도메인 객체
 */
@SuperBuilder(toBuilder = true)
@Getter
@ToString
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class Inquiry extends MemberPost {

    private final PostGroup postGroup = PostGroup.INQUIRY;

    /**
     * 비밀글 설정 여부 (true: 비밀글)
     */
    private final boolean isSecret;

    /**
     * 답변 완료 여부 (true: 답변 완료)
     */
    private final boolean isAnswerCompleted;

    /**
     * 답변 관리자의 고유 식별자
     */
    private final String managerId;

    /**
     * 비밀글의 비밀번호
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private final String password;

    /**
     * 답변 내용
     */
    private final String answerContent;

    /**
     * 답변 일자
     */
    private final String answerRegDate;

    public boolean canView(Member member) {
        if (isSecret) {
            return isAccessSecretBy(member);
        }
        return true;
    }

    public boolean isAccessSecretBy(Member member) {
        if (!member.isSameMemberId(this.getMemberId())) {
            throw new CannotViewInquiryException("다른 사용자가 작성한 문의글을 볼 수 없습니다.");
        }
        return true;
    }

    /**
     * 게시글 그룹을 반환
     *
     * @return 게시글 그룹
     */

    @Override
    public PostGroup getPostGroup() {
        return postGroup;
    }
}
