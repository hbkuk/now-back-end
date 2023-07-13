package com.now.core.post.domain.abstractions;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.now.core.category.domain.constants.PostGroup;
import com.now.core.manager.domain.Manager;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * 추상 클래스로서 {@link Manager}가 작성한 게시글을 의미하는 도메인 객체
 */
@SuperBuilder(toBuilder = true)
@Getter
@ToString
@NoArgsConstructor(force = true)
@AllArgsConstructor
public abstract class ManagerPost extends Post {

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long managerIdx;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String managerId;

    private String managerNickname;

    /**
     * 매니저의 식별자를 업데이트
     *
     * @param managerIdx 매니저 식별자
     * @return 업데이트된 ManagerPost 객체
     */
    public ManagerPost updateManagerIdx(Long managerIdx) {
        this.managerIdx = managerIdx;
        return this;
    }

    /**
     * 매니저의 아이디를 업데이트
     *
     * @param managerId 매니저 아이디
     * @return 업데이트된 ManagerPost 객체
     */
    public ManagerPost updateManagerId(String managerId) {
        this.managerId = managerId;
        return this;
    }

    /**
     * 게시글 그룹을 반환
     *
     * @return 게시글 그룹
     */
    public abstract PostGroup getPostGroup();
}
