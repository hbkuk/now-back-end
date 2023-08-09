package com.now.core.post.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
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

    private final PostGroup postGroup = PostGroup.NOTICE;

    private Long postIdx;

    @NotNull(groups = {PostValidationGroup.saveNotice.class}, message = "{post.category.notnull}")
    private final Category category;

    @NotNull(groups = {PostValidationGroup.saveNotice.class}, message = "{post.title.notnull}" )
    @Size(groups = {PostValidationGroup.saveNotice.class}, min = 1, max = 100, message = "{post.title.size}")
    private final String title;

    @NotNull(groups = {PostValidationGroup.saveNotice.class}, message = "{post.content.notnull}" )
    @Size(groups = {PostValidationGroup.saveNotice.class}, min = 1, max = 2000, message = "{post.content.size}")
    private final String content;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime regDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime modDate;

    private final Integer viewCount;

    private final Integer likeCount;

    private final Integer dislikeCount;

    @NotNull(groups = {PostValidationGroup.saveNotice.class}, message = "{post.pinned.notnull}")
    private final Boolean pinned;

    private final List<Comment> comments;

    private String managerNickname;

    @JsonIgnore
    private Integer managerIdx;

    @JsonIgnore
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
