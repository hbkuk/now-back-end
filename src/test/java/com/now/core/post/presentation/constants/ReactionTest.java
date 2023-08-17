package com.now.core.post.presentation.constants;

import com.now.core.post.presentation.dto.constants.Reaction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("반응 객체")
class ReactionTest {

    @DisplayName("저장이 가능하다면 true를 반환한다")
    @ParameterizedTest
    @MethodSource("provideReactionsForSave")
    void canSave(Reaction newReaction, boolean expected) {
        assertThat(newReaction.canSave()).isEqualTo(expected);
    }

    @DisplayName("전달받은 객체와 현재 객체를 비교해서 수정이 가능하다면 true를 반환한다")
    @ParameterizedTest
    @MethodSource("provideReactionsForUpdate")
    void canUpdate(Reaction currentReaction, Reaction newReaction, boolean expected) {
        assertThat(currentReaction.canUpdate(newReaction)).isEqualTo(expected);
    }

    private static Stream<Object[]> provideReactionsForUpdate() {
        return Stream.of(
                new Object[]{Reaction.LIKE, Reaction.LIKE, false},
                new Object[]{Reaction.LIKE, Reaction.UNDISLIKE, false},
                new Object[]{Reaction.LIKE, Reaction.UNLIKE, true},
                new Object[]{Reaction.LIKE, Reaction.DISLIKE, true},

                new Object[]{Reaction.DISLIKE, Reaction.DISLIKE, false},
                new Object[]{Reaction.DISLIKE, Reaction.UNLIKE, false},
                new Object[]{Reaction.DISLIKE, Reaction.LIKE, true},
                new Object[]{Reaction.DISLIKE, Reaction.UNDISLIKE, true},

                new Object[]{Reaction.UNLIKE, Reaction.UNLIKE, false},
                new Object[]{Reaction.UNLIKE, Reaction.UNDISLIKE, false},
                new Object[]{Reaction.UNLIKE, Reaction.LIKE, true},
                new Object[]{Reaction.UNLIKE, Reaction.DISLIKE, true},

                new Object[]{Reaction.UNDISLIKE, Reaction.UNDISLIKE, false},
                new Object[]{Reaction.UNDISLIKE, Reaction.UNLIKE, false},
                new Object[]{Reaction.UNDISLIKE, Reaction.LIKE, true},
                new Object[]{Reaction.UNDISLIKE, Reaction.DISLIKE, true}
        );
    }

    private static Stream<Object[]> provideReactionsForSave() {
        return Stream.of(
                new Object[]{Reaction.LIKE, true},
                new Object[]{Reaction.DISLIKE, true},
                new Object[]{Reaction.UNLIKE, false},
                new Object[]{Reaction.UNDISLIKE, false}
        );
    }
}
