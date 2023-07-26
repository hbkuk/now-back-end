package com.now.core.post.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.now.common.exception.ErrorType;
import com.now.core.attachment.presentation.dto.AttachmentResponse;
import com.now.core.category.domain.constants.Category;
import com.now.core.category.domain.constants.PostGroup;
import com.now.core.comment.domain.Comment;
import com.now.core.member.domain.Member;
import com.now.core.post.exception.CannotDeletePostException;
import com.now.core.post.exception.CannotUpdatePostException;
import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 커뮤니티 게시글을 나타내는 도메인 객체
 */
@Builder(toBuilder = true)
@Getter
@ToString
@NoArgsConstructor(force = true)
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Community {

    // TODO: 게시글 등록, 수정 객체 별도 관리

    private final PostGroup postGroup = PostGroup.COMMUNITY;

    private Long postIdx; // 게시글의 고유 식별자

    @NotNull(groups = {PostValidationGroup.saveCommunity.class})
    private final Category category; // 카테고리

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String managerNickname; // 매니저의 닉네임

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String memberNickname; // 회원의 닉네임

    @Size(groups = {PostValidationGroup.saveCommunity.class}, min = 1, max = 100)
    @NotEmpty(groups = {PostValidationGroup.saveCommunity.class})
    private final String title; // 게시글의 제목

    @Size(groups = {PostValidationGroup.saveCommunity.class}, min = 1, max = 2000)
    @NotEmpty(groups = {PostValidationGroup.saveCommunity.class})
    private final String content; // 게시글의 내용

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private final LocalDateTime regDate; // 게시글 등록일자

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private final LocalDateTime modDate; // 게시글 수정일자

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private final Integer viewCount; // 게시글의 조회수

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private final Integer likeCount; // 게시글의 좋아요 수

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private final Integer dislikeCount; // 게시글의 싫어요 수

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private final List<AttachmentResponse> attachments; // 파일 (attachment 테이블에서 가져옴)

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private final List<Comment> comments; // 댓글 (comment 테이블에서 가져옴)

    @JsonIgnore
    private Long memberIdx; // 회원의 고유 식별자

    @JsonIgnore
    private String memberId; // 회원의 아이디

    @JsonIgnore
    private Long managerIdx; // 매니저의 고유 식별자

    @JsonIgnore
    private String managerId; // 매니저의 아이디

    /**
     * 회원의 식별자를 업데이트
     *
     * @param memberIdx 회원 식별자
     * @return 업데이트된 MemberPost 객체
     */
    public Community updateMemberIdx(Long memberIdx) {
        this.memberIdx = memberIdx;
        return this;
    }

    /**
     * 회원의 아이디를 업데이트
     *
     * @param memberId 회원 아이디
     * @return 업데이트된 MemberPost 객체
     */
    public Community updateMemberId(String memberId) {
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
            throw new CannotUpdatePostException(ErrorType.CAN_NOT_UPDATE_OTHER_MEMBER_POST);
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
            throw new CannotDeletePostException(ErrorType.CAN_NOT_DELETE_OTHER_MEMBER_POST);
        }

        for (Comment comment : comments) {
            if (!comment.canDelete(member)) {
                throw new CannotDeletePostException(ErrorType.CAN_NOT_DELETE_POST_WITH_OTHER_MEMBER_COMMENTS);
            }
        }
        return true;
    }

    /**
     * 게시글 번호를 업데이트
     *
     * @param postIdx 게시글 번호
     * @return 업데이트된 Community 도메인 객체
     */
    public Community updatePostIdx(Long postIdx) {
        this.postIdx = postIdx;
        return this;
    }
}
