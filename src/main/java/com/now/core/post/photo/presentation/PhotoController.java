package com.now.core.post.photo.presentation;

import com.now.core.authentication.application.JwtTokenService;
import com.now.core.authentication.presentation.AuthenticationPrincipal;
import com.now.core.post.common.application.dto.AddNewAttachments;
import com.now.core.post.common.application.dto.UpdateExistingAttachments;
import com.now.core.post.photo.application.PhotoIntegratedService;
import com.now.core.post.photo.domain.Photo;
import com.now.core.post.common.domain.constants.PostValidationGroup;
import com.now.core.post.common.domain.constants.UpdateOption;
import com.now.core.post.common.presentation.dto.Condition;
import com.now.core.post.photo.presentation.dto.PhotosResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

/**
 * 게시글 관련 작업을 위한 컨트롤러
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class PhotoController {

    private final PhotoIntegratedService photoIntegratedService;

    /**
     * 모든 사진 게시글 정보를 조회하는 핸들러 메서드
     *
     * @param condition 게시물 제한 정보를 담은 객체
     * @return 모든 사진 게시글 정보와 함께 OK 응답을 반환
     */
    @GetMapping("/api/photos")
    public ResponseEntity<PhotosResponse> getAllPhotos(@Valid Condition condition) {
        return new ResponseEntity<>(
                photoIntegratedService.getAllPhotosWithPageInfo(condition.updatePage()), HttpStatus.OK);
    }

    /**
     * 사진 게시글 조회
     *
     * @param postIdx 게시글 번호
     * @return 사진 게시글 정보와 함께 OK 응답을 반환
     */
    @GetMapping("/api/photos/{postIdx}")
    public ResponseEntity<Photo> getPhoto(@PathVariable("postIdx") Long postIdx) {
        return ResponseEntity.ok(photoIntegratedService.getPhotoAndIncrementViewCount(postIdx));
    }

    /**
     * 수정 사진 게시글 조회
     *
     * @param postIdx 게시글 번호
     * @return 사진 게시글 정보와 함께 OK 응답을 반환
     */
    @GetMapping("/api/photos/{postIdx}/edit")
    public ResponseEntity<Photo> getEditPhoto(@PathVariable("postIdx") Long postIdx,
                                              @CookieValue(value = JwtTokenService.ACCESS_TOKEN_KEY, required = true) String accessToken) {
        return ResponseEntity.ok(photoIntegratedService.getEditPhoto(postIdx, accessToken));
    }

    /**
     * 사진 게시글 등록
     *
     * @param memberId    작성자의 회원 ID
     * @param photo       등록할 사진 게시글 정보
     * @param thumbnail   등록할 대표 이미지 정보
     * @param attachments 등록할 이미지 정보
     * @return 생성된 위치 URI로 응답
     */
    @PostMapping("/api/photos")
    public ResponseEntity<Void> registerPhoto(@AuthenticationPrincipal String memberId,
                                              @RequestPart(name = "photo") @Validated(PostValidationGroup.savePhoto.class) Photo photo,
                                              @RequestPart(name = "thumbnail", required = false) MultipartFile thumbnail,
                                              @RequestPart(name = "attachments", required = false) MultipartFile[] attachments) {

        photoIntegratedService.registerPhoto(photo.updateMemberId(memberId), AddNewAttachments.of(thumbnail, attachments));
        return ResponseEntity.created(URI.create("/api/photos/" + photo.getPostIdx())).build();
    }

    /**
     * 사진 게시글 수정
     *
     * @param postIdx                게시글 번호
     * @param memberId               회원 아이디
     * @param updateOption           수정 타입
     * @param updatePhoto            수정할 사진 게시글 정보
     * @param newThumbnail           새로운 대표 이미지 정보
     * @param newAttachments         새로운 이미지 정보
     * @param thumbnailAttachmentIdx 수정할 대표 이미지로 지정할 첨부 파일 번호
     * @param notDeletedIndexes      삭제하지 않을 파일 번호 목록
     * @return 생성된 위치 URI로 응답
     */
    @PutMapping("/api/photos/{postIdx}")
    public ResponseEntity<Void> updatePhoto(@PathVariable("postIdx") Long postIdx,
                                            @AuthenticationPrincipal String memberId,
                                            @RequestPart(name = "updateOption", required = true) UpdateOption updateOption,
                                            @RequestPart(name = "photo") @Validated(PostValidationGroup.savePhoto.class) Photo updatePhoto,
                                            @RequestPart(name = "thumbnail", required = false) MultipartFile newThumbnail,
                                            @RequestPart(name = "attachments", required = false) MultipartFile[] newAttachments,
                                            @RequestParam(name = "thumbnailAttachmentIdx", required = false) Long thumbnailAttachmentIdx,
                                            @RequestParam(name = "notDeletedIndexes", required = false) List<Long> notDeletedIndexes) {

        photoIntegratedService.updatePhoto(updatePhoto.updatePostIdx(postIdx).updateMemberId(memberId),
                updateOption, AddNewAttachments.of(newThumbnail, newAttachments), UpdateExistingAttachments.of(thumbnailAttachmentIdx, notDeletedIndexes));
        return ResponseEntity.created(URI.create("/api/photos/" + updatePhoto.getPostIdx())).build();
    }

    /**
     * 사진 게시글 삭제
     *
     * @param postIdx  게시글 번호
     * @param memberId 회원 아이디
     * @return 응답 본문이 없는 상태 코드 204 반환
     */
    @DeleteMapping("/api/photos/{postIdx}")
    public ResponseEntity<Void> deletePhoto(@PathVariable("postIdx") Long postIdx,
                                            @AuthenticationPrincipal String memberId) {
        photoIntegratedService.deletePhoto(postIdx, memberId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
