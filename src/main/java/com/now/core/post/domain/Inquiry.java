package com.now.core.post.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.now.core.category.domain.constants.Category;
import com.now.core.category.domain.constants.PostGroup;
import com.now.core.comment.domain.Comment;
import com.now.core.attachment.domain.Attachment;
import com.now.core.member.domain.Member;
import com.now.core.post.exception.CannotDeletePostException;
import com.now.core.post.exception.CannotUpdatePostException;
import com.now.core.post.exception.CannotViewInquiryException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 문의 게시글을 나타내는 도메인 객체
 */
@SuperBuilder(toBuilder = true)
@Getter
@ToString(callSuper = true)
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class Inquiry {

    private final PostGroup postGroup = PostGroup.INQUIRY;
    /**
     * 카테고리
     */
    private final Category category;
    /**
     * 게시글의 제목
     */
    @Size(groups = {PostValidationGroup.saveInquiry.class}, min = 1, max = 100, message = "게시글의 제목은 1글자 이상, 100글자 이하")
    private final String title;
    /**
     * 게시글 등록일자
     */
    private final LocalDateTime regDate;
    /**
     * 게시글 수정일자
     */
    private final LocalDateTime modDate;
    /**
     * 게시글의 내용
     */
    @Size(groups = {PostValidationGroup.saveInquiry.class}, min = 1, max = 2000, message = "공지사항의 내용은 1글자 이상, 2000글자 이하")
    private final String content;
    /**
     * 게시글의 조회수
     */
    private final Integer viewCount;
    /**
     * 게시글의 좋아요 수
     */
    private final Integer likeCount;
    /**
     * 게시글 싫어요 수
     */
    private final Integer dislikeCount;
    /**
     * 파일 (file 테이블에서 가져옴)
     */
    private final List<Attachment> files;
    /**
     * 댓글 (comment 테이블에서 가져옴)
     */
    private final List<Comment> comments;
    /**
     * 문의 게시글의 고유 식별자
     */
    @JsonIgnore
    private final Long inquiryIdx;
    /**
     * 비밀글 설정 여부 (true: 비밀글)
     */
    @NotNull(groups = {PostValidationGroup.saveInquiry.class}, message = "비밀글 설정 필수")
    private final Boolean secret;
    /**
     * 답변 관리자의 고유 식별자
     */
    @JsonIgnore
    private final String answerManagerIdx;
    /**
     * 답변 관리자의 고유 식별자
     */
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private final String answerManagerNickname;
    /**
     * 비밀글의 비밀번호
     */
    @Nullable
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Size(groups = {PostValidationGroup.saveInquiry.class}, min = 4, max = 15, message = "패스워드는 4글자 이상, 15글자 이하여야 합니다")
    private final String password;
    /**
     * 답변 내용
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private final String answerContent;
    /**
     * 답변 일자
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private final String answerRegDate;
    /**
     * 게시글의 고유 식별자
     */
    private Long postIdx;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long memberIdx;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String memberId;

    private String memberNickname;

    /**
     * 회원의 식별자를 업데이트
     *
     * @param memberIdx 회원 식별자
     * @return 업데이트된 MemberPost 객체
     */
    public Inquiry updateMemberIdx(Long memberIdx) {
        this.memberIdx = memberIdx;
        return this;
    }

    /**
     * 회원의 아이디를 업데이트
     *
     * @param memberId 회원 아이디
     * @return 업데이트된 MemberPost 객체
     */
    public Inquiry updateMemberId(String memberId) {
        this.memberId = memberId;
        return this;
    }

    /**
     * 게시글을 수정할 수 있다면 true 반환, 그렇지 않다면 false 반환
     *
     * @param member 수정을 시도하는 회원
     * @return 게시글을 수정할 수 있다면 true 반환, 그렇지 않다면 false 반환
     * @throws CannotUpdatePostException 다른 회원이 작성한 게시글을 수정할 수 없을 때 예외 발생
     */
    public boolean canUpdate(Member member) {
        if (!member.isSameMemberId(this.memberId)) {
            throw new CannotUpdatePostException("다른 회원이 작성한 게시글을 수정할 수 없습니다.");
        }
        return true;
    }

    /**
     * 게시글을 삭제할 수 있다면 true 반환, 그렇지 않다면 false 반환
     *
     * @param member   삭제를 시도하는 회원
     * @param comments 게시글에 포함된 댓글 목록
     * @return 게시글을 삭제할 수 있다면 true 반환, 그렇지 않다면 false 반환
     * @throws CannotDeletePostException 다른 회원이 작성한 게시글을 삭제할 수 없거나, 댓글이 존재하여 삭제할 수 없을 때 예외 발생
     */
    public boolean canDelete(Member member, List<Comment> comments) {
        if (!member.isSameMemberId(this.memberId)) {
            throw new CannotDeletePostException("다른 회원이 작성한 게시글을 삭제할 수 없습니다.");
        }

        for (Comment comment : comments) {
            if (!comment.canDelete(member)) {
                throw new CannotDeletePostException("다른 회원이 작성한 댓글이 있으므로 해당 게시글을 삭제할 수 없습니다.");
            }
        }
        return true;
    }

    public boolean canView(Member memeber) {
        if (this.secret) {
            return isAccessSecretBy(memeber);
        }
        return true;
    }

    public boolean isAccessSecretBy(Member member) {
        if (!member.isSameMemberId(this.memberId)) {
            throw new CannotViewInquiryException("다른 사용자가 작성한 문의글을 볼 수 없습니다.");
        }
        return true;
    }
}
