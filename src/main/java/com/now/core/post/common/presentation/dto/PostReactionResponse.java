package com.now.core.post.common.presentation.dto;

import com.now.core.post.common.presentation.dto.constants.Reaction;
import lombok.*;

@Builder
@Getter
@ToString
@NoArgsConstructor(force = true)
@AllArgsConstructor
@EqualsAndHashCode
public class PostReactionResponse {

    private Integer likeCount;
    private Integer dislikeCount;
    private Reaction reaction;

    /**
     * 반응이 없는 {@link PostReactionResponse} 객체를 생성
     *
     * @return 생성된 반응 없는 {@link PostReactionResponse} 객체
     */
    public PostReactionResponse createNoReactionPostReaction() {
        this.reaction = Reaction.NOTTING;
        return this;
    }
}
