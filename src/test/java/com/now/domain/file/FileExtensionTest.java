package com.now.domain.file;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class FileExtensionTest {

    @Nested
    @DisplayName("허용하는 확장자를 파일로 선정한 경우")
    class File_Extension_of {
        @DisplayName("파일로 분류한 확장자일 경우에만 객체가 생성된다.")
        @ParameterizedTest
        @ValueSource(strings = {"jpg", "gif", "png", "zip"})
        void return_true_when_file_extension(String value) {
            FileExtension extension = new FileExtension(value, FileExtensionType.FILE);
        }

        @DisplayName("파일로 분류한 확장자가 아닐 경우 예외를 던진다.")
        @ParameterizedTest
        @ValueSource(strings = {"docs", "pptx", "jsp", "html"})
        void return_true_when_not_file_extension(String value) {
            assertThatExceptionOfType(IllegalArgumentException.class)
                    .isThrownBy(() -> {
                        new FileExtension(value, FileExtensionType.FILE);
                    })
                    .withMessage("허용하지 않는 확장자입니다.");
        }
    }

    @Nested
    @DisplayName("허용하는 확장자를 이미지로 선정한 경우")
    class Image_Extension_of {

        @DisplayName("이미지로 분류한 확장자일 경우에만 객체가 생성된다.")
        @ParameterizedTest
        @ValueSource(strings = {"jpg", "gif", "png"})
        void return_true_when_image_extension(String value) {
            FileExtension extension = new FileExtension(value, FileExtensionType.IMAGE);
        }

        @DisplayName("이미지로 분류한 확장자가 아닐 경우 예외를 던진다.")
        @ParameterizedTest
        @ValueSource(strings = {"docs", "pptx", "jsp", "html"})
        void return_true_when_not_image_extension(String value) {
            assertThatExceptionOfType(IllegalArgumentException.class)
                    .isThrownBy(() -> {
                        new FileExtension(value, FileExtensionType.IMAGE);
                    })
                    .withMessage("허용하지 않는 확장자입니다.");
        }
    }
}
