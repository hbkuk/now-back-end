package com.now.core.post.common.presentation.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.now.core.post.common.presentation.dto.constants.Reaction;
import lombok.*;

import javax.validation.constraints.NotNull;

@Builder(toBuilder = true)
@Getter
@ToString
@NoArgsConstructor(force = true)
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostReaction {

    private Long postReactionIdx;

    private Long postIdx;

    @JsonIgnore
    private String managerId;

    @JsonIgnore
    private Integer managerIdx;

    @JsonIgnore
    private String managerNickname;

    @JsonIgnore
    private String memberId;

    @JsonIgnore
    private Long memberIdx;

    @JsonIgnore
    private String memberNickname;

    @NotNull(message = "{reaction.notnull}")
    private Reaction reaction;

    /**
     * 게시글 번호 업데이트
     *
     * @param postIdx 게시글 번호
     * @return 업데이트된 Comment 객체
     */
    public PostReaction updatePostIdx(Long postIdx) {
        this.postIdx = postIdx;
        return this;
    }

    /**
     * 회원 아이디 업데이트
     *
     * @param memberId 회원 아이디
     * @return 업데이트된 Comment 객체
     */
    public PostReaction updateMemberId(String memberId) {
        this.memberId = memberId;
        return this;
    }

    /**
     * 회원 번호 업데이트
     *
     * @param memberIdx 회원 번호
     * @return 업데이트된 Comment 객체
     */
    public PostReaction updateMemberIdx(Long memberIdx) {
        this.memberIdx = memberIdx;
        return this;
    }

    /**
     * 현재 반응 객체로 저장이 가능하다면 true, 그렇지 않다면 false 반환
     *
     * @return 현재 반응로 저장이 가능하다면 true, 그렇지 않다면 false 반환
     */
    public boolean canSave() {
        return this.reaction.canSave();
    }

    /**
     * 전달받은 객체와 현재 반응 객체를 비교해서 수정이 가능하다면 true, 그렇지 않다면 false 반환
     *
     * @param newPostReaction PostReaction 객체
     * @return 전달받은 객체와 현재 반응 객체를 비교해서 수정이 가능하다면 true, 그렇지 않다면 false 반환
     */
    public boolean canUpdate(PostReaction newPostReaction) {
        return this.reaction.canUpdate(newPostReaction.getReaction());
    }
}
