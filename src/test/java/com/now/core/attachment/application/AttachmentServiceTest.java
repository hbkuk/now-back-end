package com.now.core.attachment.application;

import com.now.NowApplication;
import com.now.common.exception.ErrorType;
import com.now.core.attachment.application.dto.ThumbNail;
import com.now.core.attachment.domain.Attachment;
import com.now.core.attachment.domain.AttachmentRepository;
import com.now.core.attachment.domain.constants.AttachmentType;
import com.now.core.attachment.exception.CannotUpdateThumbnailException;
import com.now.core.post.application.dto.AddNewAttachments;
import com.now.core.post.application.dto.UpdateExistingAttachments;
import com.now.core.post.domain.constants.UpdateOption;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

import static com.now.config.fixtures.attachment.AttachmentFixture.*;
import static com.now.config.fixtures.member.MemberFixture.createMember;
import static com.now.config.fixtures.post.CommunityFixture.createCommunity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = NowApplication.class)
@DisplayName("첨부 파일 서비스 객체")
class AttachmentServiceTest {

    @Autowired private AttachmentService attachmentService;
    @MockBean private AttachmentRepository attachmentRepository;

    @Nested
    @DisplayName("기존 업로드된 첨부 파일 수정")
    class EditExisting_of {

        @Nested
        @DisplayName("데이터베이스 내 게시글 번호에 해당하는 첨부 파일이 하나도 없을 때,")
        class NoAttachmentsFoundTest {

            @Test
            @DisplayName("아무런 메서드가 실행되지 않는다")
            void testUpdateAttachmentsWithThumbnail_NoAttachments() {
                Long postIdx = 1L;
                Attachment attachment = createAttachment(1L, postIdx);
                UpdateExistingAttachments updateExistingAttachments = UpdateExistingAttachments.builder()
                        .unverifiedClientThumbnailAttachmentIdx(1L)
                        .unverifiedClientExcludedIndexes(Arrays.asList(1L, 2L))

                        .existingThumbnailAttachmentIdx(null)
                        .verifiedDeletedAttachmentIndexes(null)
                        .build();
                given(attachmentRepository.findAllIndexesByPostIdx(postIdx)).willReturn(null);
                given(attachmentRepository.findThumbnailByPostIdx(postIdx)).willReturn(null);

                attachmentService.updateAttachmentsWithVerifiedIndexes(UpdateOption.EDIT_EXISTING, new AddNewAttachments(),
                        updateExistingAttachments, postIdx, AttachmentType.IMAGE);

                verify(attachmentRepository, never()).clearThumbnail(postIdx);
                verify(attachmentRepository, never()).saveAttachment(attachment);
                verify(attachmentRepository, never()).saveThumbNail(attachment);
            }

            @Test
            @DisplayName("아무런 메서드가 실행되지 않는다")
            void testUpdateAttachmentsWithThumbnail_NoAttachments_2() {
                Long postIdx = 1L;
                Attachment attachment = createAttachment(1L, postIdx);
                UpdateExistingAttachments updateExistingAttachments = UpdateExistingAttachments.builder()
                        .unverifiedClientThumbnailAttachmentIdx(null)
                        .unverifiedClientExcludedIndexes(null)

                        .existingThumbnailAttachmentIdx(null)
                        .verifiedDeletedAttachmentIndexes(null)
                        .build();
                given(attachmentRepository.findAllIndexesByPostIdx(postIdx)).willReturn(null);
                given(attachmentRepository.findThumbnailByPostIdx(postIdx)).willReturn(null);

                attachmentService.updateAttachmentsWithVerifiedIndexes(UpdateOption.EDIT_EXISTING, new AddNewAttachments(),
                        updateExistingAttachments, postIdx, AttachmentType.IMAGE);

                verify(attachmentRepository, never()).clearThumbnail(postIdx);
                verify(attachmentRepository, never()).saveAttachment(attachment);
                verify(attachmentRepository, never()).saveThumbNail(attachment);
            }
        }

        @Nested
        @DisplayName("데이터베이스 내 게시글 번호에 해당하는 첨부 파일이 있을 때,")
        class AttachmentsFoundTest {

            @Nested
            @DisplayName("첨부 파일 삭제와 대표 이미지 변경(대표 이미지 변경 혹은 초기화) 할 때,")
            class DeleteAttachmentsAndUpdateThumbnailTest {

                @Test
                @DisplayName("삭제할 첨부 파일과 대표 이미지를 초기화 한다면, deleteAttachmentsAndUpdateThumbnail 메서드가 호출된다")
                void deleteAndChangeThumbnail() {
                    Long postIdx = 1L;
                    UpdateExistingAttachments updateExistingAttachments = UpdateExistingAttachments.builder()
                            .unverifiedClientThumbnailAttachmentIdx(0L)
                            .unverifiedClientExcludedIndexes(Arrays.asList(1L, 2L))
                            .build();

                    given(attachmentRepository.findAllIndexesByPostIdx(postIdx)).willReturn(Arrays.asList(1L, 2L, 3L, 4L));
                    given(attachmentRepository.findThumbnailByPostIdx(postIdx)).willReturn(new ThumbNail(1L, postIdx, 2L));

                    given(attachmentRepository.findAttachmentResponseByAttachmentIdx(3L)).willReturn(createAttachmentResponseByAttachmentIdx(3L));
                    given(attachmentRepository.findAttachmentResponseByAttachmentIdx(4L)).willReturn(createAttachmentResponseByAttachmentIdx(4L));

                    attachmentService.updateAttachmentsWithVerifiedIndexes(UpdateOption.EDIT_EXISTING, new AddNewAttachments(),
                            updateExistingAttachments, postIdx, AttachmentType.IMAGE);

                    verify(attachmentRepository, times(1)).clearThumbnail(postIdx);
                    verify(attachmentRepository, times(1)).deleteAttachmentIdx(3L);
                    verify(attachmentRepository, times(1)).deleteAttachmentIdx(4L);
                }

                @Test
                @DisplayName("삭제할 첨부 파일과 대표 이미지 변경 중 존재하지 않는 대표 이미지로 변경하려고 한다면 CannotUpdateThumbnailException을 던진다")
                void cannotUpdateThumbnailException() {
                    Long postIdx = 1L;
                    UpdateExistingAttachments updateExistingAttachments = UpdateExistingAttachments.builder()
                            .unverifiedClientThumbnailAttachmentIdx(100L)
                            .unverifiedClientExcludedIndexes(Arrays.asList(1L, 2L))
                            .build();

                    given(attachmentRepository.findAllIndexesByPostIdx(postIdx)).willReturn(Arrays.asList(1L, 2L, 3L, 4L));
                    given(attachmentRepository.findThumbnailByPostIdx(postIdx)).willReturn(new ThumbNail(1L, postIdx, 2L));

                    given(attachmentRepository.findAttachmentResponseByAttachmentIdx(3L)).willReturn(createAttachmentResponseByAttachmentIdx(3L));
                    given(attachmentRepository.findAttachmentResponseByAttachmentIdx(4L)).willReturn(createAttachmentResponseByAttachmentIdx(4L));

                    assertThatExceptionOfType(CannotUpdateThumbnailException.class)
                            .isThrownBy(() -> {
                                attachmentService.updateAttachmentsWithVerifiedIndexes(UpdateOption.EDIT_EXISTING, new AddNewAttachments(),
                                        updateExistingAttachments, postIdx, AttachmentType.IMAGE);
                            })
                            .withMessageMatching(ErrorType.CAN_NOT_UPDATE_THUMBNAIL.getMessage());
                }

                @Test
                @DisplayName("삭제할 첨부 파일과 변경할 대표 이미지가 있다면 deleteAttachmentsAndUpdateThumbnail 메서드가 실행된다")
                void deleteAttachmentsAndChangeThumbnail() {
                    Long postIdx = 1L;
                    Attachment attachment = createAttachment(postIdx, 1L);
                    UpdateExistingAttachments updateExistingAttachments = UpdateExistingAttachments.builder()
                            .unverifiedClientThumbnailAttachmentIdx(1L)
                            .unverifiedClientExcludedIndexes(Arrays.asList(1L, 2L))
                            .build();

                    given(attachmentRepository.findAllIndexesByPostIdx(postIdx)).willReturn(Arrays.asList(1L, 2L, 3L, 4L));
                    given(attachmentRepository.findThumbnailByPostIdx(postIdx)).willReturn(new ThumbNail(1L, postIdx, 2L));

                    given(attachmentRepository.findAttachmentResponseByAttachmentIdx(3L)).willReturn(createAttachmentResponseByAttachmentIdx(3L));
                    given(attachmentRepository.findAttachmentResponseByAttachmentIdx(4L)).willReturn(createAttachmentResponseByAttachmentIdx(4L));

                    attachmentService.updateAttachmentsWithVerifiedIndexes(UpdateOption.EDIT_EXISTING, new AddNewAttachments(),
                            updateExistingAttachments, postIdx, AttachmentType.IMAGE);

                    verify(attachmentRepository, times(1)).updateThumbnail(attachment);
                    verify(attachmentRepository, times(1)).deleteAttachmentIdx(3L);
                    verify(attachmentRepository, times(1)).deleteAttachmentIdx(4L);
                }

                @Test
                @DisplayName("삭제할 첨부 파일과 대표 이미지를 삭제하려고 한다면, deleteAttachmentsAndUpdateThumbnail 메서드가 실행된다.")
                void clearThumbnail() {
                    Long postIdx = 1L;
                    UpdateExistingAttachments updateExistingAttachments = UpdateExistingAttachments.builder()
                            .unverifiedClientThumbnailAttachmentIdx(0L)
                            .unverifiedClientExcludedIndexes(List.of())
                            .build();

                    given(attachmentRepository.findAllIndexesByPostIdx(postIdx)).willReturn(Arrays.asList(1L, 2L, 3L, 4L));
                    given(attachmentRepository.findThumbnailByPostIdx(postIdx)).willReturn(new ThumbNail(1L, postIdx, 4L));

                    given(attachmentRepository.findAttachmentResponseByAttachmentIdx(1L)).willReturn(createAttachmentResponseByAttachmentIdx(1L));
                    given(attachmentRepository.findAttachmentResponseByAttachmentIdx(2L)).willReturn(createAttachmentResponseByAttachmentIdx(2L));
                    given(attachmentRepository.findAttachmentResponseByAttachmentIdx(3L)).willReturn(createAttachmentResponseByAttachmentIdx(3L));
                    given(attachmentRepository.findAttachmentResponseByAttachmentIdx(4L)).willReturn(createAttachmentResponseByAttachmentIdx(4L));

                    attachmentService.updateAttachmentsWithVerifiedIndexes(UpdateOption.EDIT_EXISTING, new AddNewAttachments(),
                            updateExistingAttachments, postIdx, AttachmentType.IMAGE);

                    verify(attachmentRepository, times(1)).clearThumbnail(postIdx);
                    verify(attachmentRepository, times(1)).deleteAttachmentIdx(1L);
                    verify(attachmentRepository, times(1)).deleteAttachmentIdx(2L);
                    verify(attachmentRepository, times(1)).deleteAttachmentIdx(3L);
                    verify(attachmentRepository, times(1)).deleteAttachmentIdx(4L);
                }
            }

            @Nested
            @DisplayName("대표 이미지만 변경(대표 이미지 변경 혹은 초기화) 할 때,")
            class UpdateOnlyThumbnailTest {

                @Test
                @DisplayName("대표 이미지만 변경 하려고 한다면, updateOnlyThumbnail 메서드가 실행된다.")
                void clearThumbnail() {
                    Long postIdx = 1L;
                    UpdateExistingAttachments updateExistingAttachments = UpdateExistingAttachments.builder()
                            .unverifiedClientThumbnailAttachmentIdx(0L)
                            .unverifiedClientExcludedIndexes(Arrays.asList(1L, 2L, 3L, 4L))
                            .build();

                    given(attachmentRepository.findAllIndexesByPostIdx(postIdx)).willReturn(Arrays.asList(1L, 2L, 3L, 4L));
                    given(attachmentRepository.findThumbnailByPostIdx(postIdx)).willReturn(new ThumbNail(1L, postIdx, 4L));

                    attachmentService.updateAttachmentsWithVerifiedIndexes(UpdateOption.EDIT_EXISTING, new AddNewAttachments(),
                            updateExistingAttachments, postIdx, AttachmentType.IMAGE);

                    verify(attachmentRepository, times(1)).clearThumbnail(postIdx);
                }

                @Test
                @DisplayName("대표 이미지 변경 중 존재하지 않는 대표 이미지로 변경하려고 한다면 CannotUpdateThumbnailException을 던진다")
                void cannotUpdateThumbnailException() {
                    Long postIdx = 1L;
                    UpdateExistingAttachments updateExistingAttachments = UpdateExistingAttachments.builder()
                            .unverifiedClientThumbnailAttachmentIdx(100L)
                            .unverifiedClientExcludedIndexes(Arrays.asList(1L, 2L, 3L, 4L))
                            .build();

                    given(attachmentRepository.findAllIndexesByPostIdx(postIdx)).willReturn(Arrays.asList(1L, 2L, 3L, 4L));
                    given(attachmentRepository.findThumbnailByPostIdx(postIdx)).willReturn(new ThumbNail(1L, postIdx, 4L));

                    assertThatExceptionOfType(CannotUpdateThumbnailException.class)
                            .isThrownBy(() -> {
                                attachmentService.updateAttachmentsWithVerifiedIndexes(UpdateOption.EDIT_EXISTING, new AddNewAttachments(),
                                        updateExistingAttachments, postIdx, AttachmentType.IMAGE);
                            })
                            .withMessageMatching(ErrorType.CAN_NOT_UPDATE_THUMBNAIL.getMessage());
                }
            }

            @Nested
            @DisplayName("첨부 파일만 삭제할 때,")
            class DeleteOnlyAttachmentsTest  {

                @Test
                @DisplayName("파일 삭제만 하려고 한다면, verifiedUpdateExistingAttachments 메서드가 실행된다.")
                void deleteAttachments() {
                    Long postIdx = 1L;
                    UpdateExistingAttachments updateExistingAttachments = UpdateExistingAttachments.builder()
                            .unverifiedClientThumbnailAttachmentIdx(1L)
                            .unverifiedClientExcludedIndexes(Arrays.asList(1L, 2L))
                            .build();

                    given(attachmentRepository.findAllIndexesByPostIdx(postIdx)).willReturn(Arrays.asList(1L, 2L, 3L, 4L));
                    given(attachmentRepository.findThumbnailByPostIdx(postIdx)).willReturn(new ThumbNail(1L, postIdx, 1L));

                    given(attachmentRepository.findAttachmentResponseByAttachmentIdx(3L)).willReturn(createAttachmentResponseByAttachmentIdx(3L));
                    given(attachmentRepository.findAttachmentResponseByAttachmentIdx(4L)).willReturn(createAttachmentResponseByAttachmentIdx(4L));

                    attachmentService.updateAttachmentsWithVerifiedIndexes(UpdateOption.EDIT_EXISTING, new AddNewAttachments(),
                            updateExistingAttachments, postIdx, AttachmentType.IMAGE);

                    verify(attachmentRepository, times(1)).deleteAttachmentIdx(3L);
                    verify(attachmentRepository, times(1)).deleteAttachmentIdx(4L);
                }
            }
        }
    }

    @Nested
    @DisplayName("새롭게 업로드된 첨부 파일 추가")
    class AddNew_of {

        @Nested
        @DisplayName("업로드된 새로운 첨부 파일이 없을 때,")
        class NoAttachmentsFoundTest {

            @Test
            @DisplayName("아무런 메서드가 실행되지 않는다")
            void testAddNewAttachments_NoAttachments() {
                Long postIdx = 1L;
                Attachment attachment = createAttachment(1L, postIdx);
                AddNewAttachments addNewAttachments = AddNewAttachments.builder()
                        .newThumbnail(null)
                        .newAttachments(null)
                        .build();

                attachmentService.updateAttachmentsWithVerifiedIndexes(UpdateOption.ADD_NEW, addNewAttachments,
                        new UpdateExistingAttachments(), postIdx, AttachmentType.IMAGE);

                verify(attachmentRepository, never()).saveAttachment(attachment);
                verify(attachmentRepository, never()).updateThumbnail(attachment);
            }
        }

        @Nested
        @DisplayName("업로드된 새로운 첨부파일 있을 때,")
        class NewAttachments {

            @Nested
            @DisplayName("게시물 번호에 해당하는 대표 이미지 정보가 없다면")
            class NotExistingThumbnail {

                @Test
                @DisplayName("새로운 대표 이미지와 첨부 파일 목록이라면, saveAttachmentsWithUpdateThumbnail 메서드가 실행된다")
                void testAddNewAttachments() {
                    Long postIdx = 1L;
                    AddNewAttachments addNewAttachments = AddNewAttachments.builder()
                            .newThumbnail(createMockMultipartFile("thumbnail.jpg"))
                            .newAttachments(new MultipartFile[]{createMockMultipartFile("attachment2.jpg"), createMockMultipartFile("attachment2.jpg")})
                            .build();

                    given(attachmentRepository.findThumbnailByPostIdx(postIdx)).willReturn(null);

                    attachmentService.updateAttachmentsWithVerifiedIndexes(UpdateOption.ADD_NEW, addNewAttachments,
                            new UpdateExistingAttachments(), postIdx, AttachmentType.IMAGE);

                    verify(attachmentRepository, times(3)).saveAttachment(any());
                    verify(attachmentRepository, times(1)).saveThumbNail(any());
                }

                @Test
                @DisplayName("새로운 대표 이미지만 업로드 되었다면, updateThumbnail 메서드가 실행된다")
                void testAddOnlyNewThumbnail() {
                    Long postIdx = 1L;
                    AddNewAttachments addNewAttachments = AddNewAttachments.builder()
                            .newThumbnail(createMockMultipartFile("thumbnail.jpg"))
                            .build();

                    given(attachmentRepository.findThumbnailByPostIdx(postIdx)).willReturn(null);

                    attachmentService.updateAttachmentsWithVerifiedIndexes(UpdateOption.ADD_NEW, addNewAttachments,
                            new UpdateExistingAttachments(), postIdx, AttachmentType.IMAGE);

                    verify(attachmentRepository, times(1)).saveAttachment(any());
                    verify(attachmentRepository, times(1)).saveThumbNail(any());
                }


            }

            @Nested
            @DisplayName("게시물 번호에 해당하는 대표 이미지 정보가 있다면")
            class ExistingThumbnail {

                @Test
                @DisplayName("새로운 대표 이미지와 첨부 파일 목록이라면, saveAttachmentsWithUpdateThumbnail 메서드가 실행된다")
                void testAddNewAttachments() {
                    Long postIdx = 1L;
                    AddNewAttachments addNewAttachments = AddNewAttachments.builder()
                            .newThumbnail(createMockMultipartFile("thumbnail.jpg"))
                            .newAttachments(new MultipartFile[]{createMockMultipartFile("attachment2.jpg"), createMockMultipartFile("attachment2.jpg")})
                            .build();

                    given(attachmentRepository.findThumbnailByPostIdx(postIdx))
                                        .willReturn(new ThumbNail(1L, postIdx, 3L));

                    attachmentService.updateAttachmentsWithVerifiedIndexes(UpdateOption.ADD_NEW, addNewAttachments,
                            new UpdateExistingAttachments(), postIdx, AttachmentType.IMAGE);

                    verify(attachmentRepository, times(3)).saveAttachment(any());
                    verify(attachmentRepository, times(1)).updateThumbnail(any());
                }

                @Test
                @DisplayName("새로운 대표 이미지만 업로드 되었다면, updateThumbnail 메서드가 실행된다")
                void testAddOnlyNewThumbnail() {
                    Long postIdx = 1L;
                    AddNewAttachments addNewAttachments = AddNewAttachments.builder()
                            .newThumbnail(createMockMultipartFile("thumbnail.jpg"))
                            .build();

                    given(attachmentRepository.findThumbnailByPostIdx(postIdx))
                                            .willReturn(new ThumbNail(1L, postIdx, 3L));

                    attachmentService.updateAttachmentsWithVerifiedIndexes(UpdateOption.ADD_NEW, addNewAttachments,
                            new UpdateExistingAttachments(), postIdx, AttachmentType.IMAGE);

                    verify(attachmentRepository, times(1)).saveAttachment(any());
                    verify(attachmentRepository, times(1)).updateThumbnail(any());
                }

            }

            @Test
            @DisplayName("새로운 첨부 파일만 업로드 되었다면, saveAttachments 메서드가 실행된다")
            void testAddNewAttachmentsWithoutThumbnail() {
                Long postIdx = 1L;
                AddNewAttachments addNewAttachments = AddNewAttachments.builder()
                        .newAttachments(new MultipartFile[]{createMockMultipartFile("attachment2.jpg"), createMockMultipartFile("attachment2.jpg")})
                        .build();

                attachmentService.updateAttachmentsWithVerifiedIndexes(UpdateOption.ADD_NEW, addNewAttachments,
                        new UpdateExistingAttachments(), postIdx, AttachmentType.IMAGE);

                verify(attachmentRepository, times(2)).saveAttachment(any());
            }
        }
    }
}
