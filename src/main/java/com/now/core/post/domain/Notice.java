package com.now.core.post.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.now.core.admin.manager.domain.Manager;
import com.now.core.category.domain.constants.Category;
import com.now.core.category.domain.constants.PostGroup;
import com.now.core.comment.domain.Comment;
import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 공지사항 게시글을 나타내는 도메인 객체
 */
@Builder(toBuilder = true)
@Getter
@ToString(callSuper = true)
@NoArgsConstructor(force = true)
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Notice {

    // TODO: 게시글 등록, 수정 객체 별도 관리

    private final PostGroup postGroup = PostGroup.NOTICE;

    private Long postIdx; // 공지 게시글의 고유 식별자

    @NotNull(groups = {PostValidationGroup.saveNotice.class})
    private final Category category; // 카테고리

    private String managerNickname; // 매니저 닉네임

    @Size(groups = {PostValidationGroup.saveNotice.class}, min = 1, max = 100)
    private final String title; // 제목

    @Size(groups = {PostValidationGroup.saveNotice.class}, min = 1, max = 2000)
    private final String content; // 내용

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime regDate; // 등록일자

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime modDate; // 수정일자

    private final Integer viewCount; // 조회수

    private final Integer likeCount; // 좋아요 수

    private final Integer dislikeCount; // 싫어요 수

    @NotNull(groups = {PostValidationGroup.saveNotice.class})  // 상단 고정 여부 (true: 상단 고정)
    private final Boolean pinned;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private final List<Comment> comments; // 댓글 (comment 테이블에서 가져옴)

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) // 매니저 고유 식별자
    private Integer managerIdx;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) // 매니저 아이디
    private String managerId;

    /**
     * 매니저의 식별자를 업데이트
     *
     * @param managerIdx 매니저 식별자
     * @return 업데이트된 Notice 도메인 객체
     */
    public Notice updateManagerIdx(Integer managerIdx) {
        this.managerIdx = managerIdx;
        return this;
    }

    /**
     * 매니저의 아이디를 업데이트
     *
     * @param managerId 매니저 아이디
     * @return 업데이트된 Notice 도메인 객체
     */
    public Notice updateManagerId(String managerId) {
        this.managerId = managerId;
        return this;
    }

    /**
     * 게시글 번호를 업데이트
     *
     * @param postIdx 게시글 번호
     * @return 업데이트된 Notice 도메인 객체
     */
    public Notice updatePostIdx(Long postIdx) {
        this.postIdx = postIdx;
        return this;
    }

    // TODO: 추후 매니저별 권한 부여
    /**
     * 게시글을 수정할 수 있다면 true를 반환, 그렇지 않다면 false 반환
     *
     * @param manager 매니저 도메인 객체
     * @return 게시글을 수정할 수 있다면 true를 반환, 그렇지 않다면 false 반환
     */
    public boolean canUpdate(Manager manager) {
        return true;
    }

    // TODO: 추후 매니저별 권한 부여
    /**
     * 게시글을 삭제할 수 있다면 true를 반환, 그렇지 않다면 false 반환
     *
     * @param manager 매니저 도메인 객체
     * @return 게시글을 삭제할 수 있다면 true를 반환, 그렇지 않다면 false 반환
     */
    public boolean canDelete(Manager manager) {
        return true;
    }
}
