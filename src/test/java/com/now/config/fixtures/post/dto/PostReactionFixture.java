package com.now.config.fixtures.post.dto;

import com.now.core.post.common.presentation.dto.PostReaction;
import com.now.core.post.common.presentation.dto.PostReactionResponse;
import com.now.core.post.common.presentation.dto.constants.Reaction;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class PostReactionFixture {

    public static PostReactionResponse createPostReactionResponse(Integer likeCount, Integer dislikeCount, Reaction reaction) {
        return PostReactionResponse.builder()
                .likeCount(likeCount)
                .dislikeCount(dislikeCount)
                .reaction(reaction)
                .build();
    }

    public static PostReactionResponse createPostReactionResponse(Reaction reaction) {
        return PostReactionResponse.builder()
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

    public static PostReaction createPostReactionByManager(Long postIdx, Long managerIdx, Reaction reaction) {
        return PostReaction.builder()
                .postIdx(postIdx)
                .managerIdx(managerIdx)
                .reaction(reaction)
                .build();
    }

    public static PostReaction createPostReaction(Long postIdx, Long memberIdx, String memberId, Reaction reaction) {
        return PostReaction.builder()
                .postIdx(postIdx)
                .memberIdx(memberIdx)
                .memberId(memberId)
                .reaction(reaction)
                .build();
    }

    public static PostReaction createPostReaction(Long postIdx, String memberId, Reaction reaction) {
        return PostReaction.builder()
                .postIdx(postIdx)
                .memberId(memberId)
                .reaction(reaction)
                .build();
    }

    public static PostReaction createPostReaction(Long postIdx, Long memberIdx) {
        return PostReaction.builder()
                .postIdx(postIdx)
                .memberIdx(memberIdx)
                .reaction(null)
                .build();
    }

    @Test
    void create() {
        Long postIdx = 1L;
        Long memberIdx = 1L;

        assertThat(PostReaction.create(postIdx, memberIdx)).isEqualTo(PostReactionFixture.createPostReaction(postIdx, memberIdx));
    }
}
