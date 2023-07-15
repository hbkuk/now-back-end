package com.now.core.post.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.now.core.category.domain.constants.Category;
import com.now.core.category.domain.constants.PostGroup;
import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * 공지사항 게시글을 나타내는 도메인 객체
 */
@Builder(toBuilder = true)
@Getter
@ToString(callSuper = true)
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class Notice {

    private final PostGroup postGroup = PostGroup.NOTICE;

    /**
     * 공지 게시글의 고유 식별자
     */
    private Long postIdx;

    /**
     * 카테고리
     */
    private final Category category;

    /**
     * 게시글의 제목
     */
    @Size(groups = {PostValidationGroup.saveNotice.class}, min = 1, max = 100, message = "공지사항의 제목은 1글자 이상, 100글자 이하")
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
    @Size(groups = {PostValidationGroup.saveNotice.class}, min = 1, max = 2000, message = "공지사항의 내용은 1글자 이상, 2000글자 이하")
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
     * 공지사항의 상단 고정 여부 (true: 상단 고정)
     */
    @NotNull(groups = {PostValidationGroup.saveNotice.class}, message = "핀 설정 필수")
    private final Boolean pinned;
    /**
     * 매니저 고유 식별자
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long managerIdx;
    /**
     * 매니저 아이디
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String managerId;
    /**
     * 매니저 닉네임
     */
    private String managerNickname;

    /**
     * 매니저의 식별자를 업데이트
     *
     * @param managerIdx 매니저 식별자
     * @return 업데이트된 ManagerPost 객체
     */
    public Notice updateManagerIdx(Long managerIdx) {
        this.managerIdx = managerIdx;
        return this;
    }

    /**
     * 매니저의 아이디를 업데이트
     *
     * @param managerId 매니저 아이디
     * @return 업데이트된 ManagerPost 객체
     */
    public Notice updateManagerId(String managerId) {
        this.managerId = managerId;
        return this;
    }
}
