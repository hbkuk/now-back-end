package com.now.domain.file;

import com.now.core.file.domain.constants.UploadType;
import com.now.core.file.domain.wrapped.FileSize;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

public class FileSizeTest {

    @Nested
    @DisplayName("파일 사이즈는")
    class File_Size_of {
        
        @Nested
        @DisplayName("파일일 경우")
        class File {

            @Test
            @DisplayName("2MB 이하일 경우에만 객체가 생성된다.")
            void must_be_within_2MB() {
                FileSize fileSize = new FileSize(2048000, UploadType.FILE.getMaxUploadSize());
            }

            @Test
            @DisplayName("2MB를 초과할 경우 예외가 발생한다.")
            void throw_exception_when_exceeds_2MB() {
                assertThatExceptionOfType(IllegalArgumentException.class)
                        .isThrownBy(() -> {
                            new FileSize(2048001, UploadType.FILE.getMaxUploadSize());
                        })
                        .withMessage("허용하지 않는 파일 크기입니다.");

            }
        }

        @Nested
        @DisplayName("이미지일 경우")
        class Image {

            @Test
            @DisplayName("1MB 이하일 경우에만 객체가 생성된다.")
            void must_be_within_1MB() {
                FileSize fileSize = new FileSize(1024000, UploadType.IMAGE.getMaxUploadSize());
            }

            @Test
            @DisplayName("1MB를 초과할 경우 예외가 발생한다.")
            void throw_exception_when_exceeds_1MB() {
                assertThatExceptionOfType(IllegalArgumentException.class)
                        .isThrownBy(() -> {
                            new FileSize(1024001, UploadType.IMAGE.getMaxUploadSize());
                        })
                        .withMessage("허용하지 않는 파일 크기입니다.");
            }
        }
    }
}
