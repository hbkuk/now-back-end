package com.now.core.post.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
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

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 사진 게시글을 나타내는 도메인 객체
 */
@Builder(toBuilder = true)
@Getter
@ToString(callSuper = true)
@NoArgsConstructor(force = true)
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Photo {

    // TODO: 첨부파일 설정에 대한 로직
    // TODO: 게시글 등록, 수정 객체 별도 관리

    private final PostGroup postGroup = PostGroup.PHOTO;

    private Long postIdx;   // 게시글의 고유 식별자

    @NotNull(groups = {PostValidationGroup.savePhoto.class}, message = "{post.category.notnull}")
    private final Category category; // 카테고리

    private String memberNickname;  // 회원의 닉네임

    @Size(groups = {PostValidationGroup.savePhoto.class}, min = 1, max = 100, message = "{post.title.size}")
    private final String title; // 게시글의 제목

    @Size(groups = {PostValidationGroup.savePhoto.class}, min = 1, max = 2000, message = "{post.content.size}")
    private final String content;   // 게시글의 내용

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime regDate;    // 게시글 등록일자

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime modDate;    // 게시글 수정일자

    private final Integer viewCount;    // 게시글의 조회수

    private final Integer likeCount;    // 게시글의 좋아요 수

    private final Integer dislikeCount; // 게시글 싫어요 수

    private final List<AttachmentResponse> attachments; // 파일 (attachment 테이블에서 가져옴)

    private final List<Comment> comments;   // 댓글 (comment 테이블에서 가져옴)

    private final Long thumbnailAttachmentIdx;  // 대표 사진으로 설정된 파일의 고유 식별자

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long memberIdx; // 회원의 고유 식별자

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String memberId;    // 회원의 아이디

    /**
     * 회원의 식별자를 업데이트
     *
     * @param memberIdx 회원 식별자
     * @return 업데이트된 MemberPost 객체
     */
    public Photo updateMemberIdx(Long memberIdx) {
        this.memberIdx = memberIdx;
        return this;
    }

    /**
     * 회원의 아이디를 업데이트
     *
     * @param memberId 회원 아이디
     * @return 업데이트된 MemberPost 객체
     */
    public Photo updateMemberId(String memberId) {
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
     * 게시글 번호 수정 후 해당 객체 반환
     *
     * @param postIdx 수정할 게시물 번호
     * @return 게시물 번호 필드를 수정한 해당 객체
     */
    public Photo updatePostIdx(Long postIdx) {
        this.postIdx = postIdx;
        return this;
    }
}
