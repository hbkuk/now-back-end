package com.now.core.attachment.domain.wrapped;

import com.now.core.attachment.domain.constants.AttachmentType;
import com.now.core.attachment.exception.InvalidAttachmentException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@DisplayName("파일 크기는")
public class AttachmentSizeTest {

    @Nested
    @DisplayName("파일일 경우")
    class Attachment {

        @Test
        @DisplayName("2MB 이하일 경우에만 객체가 생성된다.")
        void must_be_within_2MB() {
            AttachmentSize attachmentSize = new AttachmentSize(2048000, AttachmentType.FILE.getMaxUploadSize());
        }

        @Test
        @DisplayName("2MB를 초과할 경우 예외가 발생한다.")
        void throw_exception_when_exceeds_2MB() {
            assertThatExceptionOfType(InvalidAttachmentException.class)
                    .isThrownBy(() -> {
                        new AttachmentSize(2048001, AttachmentType.FILE.getMaxUploadSize());
                    })
                    .withMessage("허용하지 않은 첨부 파일의 크기입니다.");

        }
    }

    @Nested
    @DisplayName("이미지일 경우")
    class Image {

        @Test
        @DisplayName("2MB 이하일 경우에만 객체가 생성된다.")
        void must_be_within_2MB() {
            AttachmentSize attachmentSize = new AttachmentSize(2048000, AttachmentType.IMAGE.getMaxUploadSize());
        }

        @Test
        @DisplayName("2MB를 초과할 경우 예외가 발생한다.")
        void throw_exception_when_exceeds_1MB() {
            assertThatExceptionOfType(InvalidAttachmentException.class)
                    .isThrownBy(() -> {
                        new AttachmentSize(2048001, AttachmentType.IMAGE.getMaxUploadSize());
                    })
                    .withMessage("허용하지 않은 첨부 파일의 크기입니다.");
        }
    }
}
