package com.now.core.attachment.domain.wrapped;

import com.now.core.attachment.domain.constants.AttachmentType;
import com.now.core.attachment.exception.InvalidAttachmentException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@DisplayName("파일 확장자는")
public class AttachmentExtensionTest {

    @Nested
    @DisplayName("허용하는 확장자를 파일로 선정한 경우")
    class Attachment_Extension_of {

        @DisplayName("파일로 분류한 확장자일 경우에만 객체가 생성된다.")
        @ParameterizedTest
        @ValueSource(strings = {"jpg", "gif", "png", "zip"})
        void return_true_when_attachment_extension(String value) {
            AttachmentExtension extension = new AttachmentExtension(value, AttachmentType.FILE.getAllowedExtensions());
        }

        @DisplayName("파일로 분류한 확장자가 아닐 경우 예외를 던진다.")
        @ParameterizedTest
        @ValueSource(strings = {"docs", "pptx", "jsp", "html"})
        void return_true_when_not_attachment_extension(String value) {
            assertThatExceptionOfType(InvalidAttachmentException.class)
                    .isThrownBy(() -> {
                        new AttachmentExtension(value, AttachmentType.FILE.getAllowedExtensions());
                    })
                    .withMessage("허용하지 않은 첨부 파일의 확장자입니다.");
        }
    }

    @Nested
    @DisplayName("허용하는 확장자를 이미지로 선정한 경우")
    class Image_Extension_of {

        @DisplayName("이미지로 분류한 확장자일 경우에만 객체가 생성된다.")
        @ParameterizedTest
        @ValueSource(strings = {"jpg", "gif", "png"})
        void return_true_when_image_extension(String value) {
            AttachmentExtension extension = new AttachmentExtension(value, AttachmentType.IMAGE.getAllowedExtensions());
        }

        @DisplayName("이미지로 분류한 확장자가 아닐 경우 예외를 던진다.")
        @ParameterizedTest
        @ValueSource(strings = {"docs", "pptx", "jsp", "html"})
        void return_true_when_not_image_extension(String value) {
            assertThatExceptionOfType(InvalidAttachmentException.class)
                    .isThrownBy(() -> {
                        new AttachmentExtension(value, AttachmentType.IMAGE.getAllowedExtensions());
                    })
                    .withMessage("허용하지 않은 첨부 파일의 확장자입니다.");
        }
    }
}
