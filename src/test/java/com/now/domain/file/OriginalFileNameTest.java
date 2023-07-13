package com.now.domain.file;

import com.now.core.file.domain.wrapped.OriginalFileName;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class OriginalFileNameTest {

    @Nested
    @DisplayName("파일 이름은")
    class File_Name_of {

        @Test
        @DisplayName("500자 이내일 경우 객체가 생성된다.")
        void must_be_within_500_characters() {
            // given
            StringBuilder characters = new StringBuilder("가");
            while (characters.length() != 500) {
                characters.append("가");
            }

            OriginalFileName originalName = new OriginalFileName(characters.toString());
        }

        @Test
        @DisplayName("500자를 초과한다면 예외가 발생한다.")
        void throw_exception_when_exceeds_500_characters() {
            // given
            StringBuilder characters = new StringBuilder("가");
            while (characters.length() != 501) {
                characters.append("가");
            }

            assertThatExceptionOfType(IllegalArgumentException.class)
                    .isThrownBy(() -> {
                        new OriginalFileName(characters.toString());
                    })
                    .withMessage("파일 이름은 500자를 초과할 수 없습니다.");
        }
    }
}
