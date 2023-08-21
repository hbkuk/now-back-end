package com.now.config.fixtures.post.dto;

import com.now.core.post.common.presentation.dto.PostReaction;
import com.now.core.post.common.presentation.dto.constants.Reaction;

public class PostReactionFixture {

    public static PostReaction createPostReaction(Reaction reaction) {
        return PostReaction.builder()
                .reaction(reaction)
                .build();
    }

    public static PostReaction createPostReaction(Long postIdx, Long memberIdx, Reaction reaction) {
        return PostReaction.builder()
                .postIdx(postIdx)
                .memberIdx(memberIdx)
                .reaction(reaction)
                .build();
    }

    public static PostReaction createPostReaction(Long postIdx, Integer managerIdx, Reaction reaction) {
        return PostReaction.builder()
                .postIdx(postIdx)
                .managerIdx(managerIdx)
                .reaction(reaction)
                .build();
    }
}
