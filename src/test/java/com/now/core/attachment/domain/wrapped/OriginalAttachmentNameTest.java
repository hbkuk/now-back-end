package com.now.core.attachment.domain.wrapped;

import com.now.core.attachment.domain.wrapped.OriginalAttachmentName;
import com.now.core.attachment.exception.InvalidAttachmentException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@DisplayName("파일명은")
public class OriginalAttachmentNameTest {

    @Test
    @DisplayName("500자 이내일 경우 객체가 생성된다.")
    void must_be_within_500_characters() {
        // given
        StringBuilder characters = new StringBuilder("가");
        while (characters.length() != 500) {
            characters.append("가");
        }

        OriginalAttachmentName originalName = new OriginalAttachmentName(characters.toString());
    }

    @Test
    @DisplayName("500자를 초과한다면 예외가 발생한다.")
    void throw_exception_when_exceeds_500_characters() {
        // given
        StringBuilder characters = new StringBuilder("가");
        while (characters.length() != 501) {
            characters.append("가");
        }

        assertThatExceptionOfType(InvalidAttachmentException.class)
                .isThrownBy(() -> {
                    new OriginalAttachmentName(characters.toString());
                })
                .withMessage("허용하지 않은 첨부 파일명입니다.");
    }
}
