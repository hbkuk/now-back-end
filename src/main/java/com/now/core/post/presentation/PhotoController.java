package com.now.core.post.presentation;

import com.now.core.attachment.application.AttachmentService;
import com.now.core.attachment.domain.constants.AttachmentType;
import com.now.core.authentication.application.JwtTokenService;
import com.now.core.comment.application.CommentService;
import com.now.core.post.application.PhotoService;
import com.now.core.post.application.dto.AddNewAttachments;
import com.now.core.post.application.dto.UpdateExistingAttachments;
import com.now.core.post.domain.Photo;
import com.now.core.post.domain.PostValidationGroup;
import com.now.core.post.domain.constants.UpdateOption;
import com.now.core.post.presentation.dto.Condition;
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

import static com.now.common.utils.AttachmentUtils.moveFileToFront;

/**
 * 게시글 관련 작업을 위한 컨트롤러
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class PhotoController {

    private final JwtTokenService jwtTokenService;
    private final PhotoService photoService;
    private final AttachmentService attachmentService;
    private final CommentService commentService;

    /**
     * 모든 사진 게시글 정보를 조회하는 핸들러 메서드
     *
     * @param condition 게시물 제한 정보를 담은 객체
     * @return 모든 사진 게시글 정보와 함께 OK 응답을 반환
     */
    @GetMapping("/api/photos")
    public ResponseEntity<List<Photo>> getAllPhotos(@Valid @ModelAttribute Condition condition) {
        log.debug("getAllPhotos 호출, condition : {}", condition);
        return new ResponseEntity<>(photoService.getAllPhotos(condition), HttpStatus.OK);
    }

    /**
     * 사진 게시글 조회
     *
     * @param postIdx 게시글 번호
     * @return 사진 게시글 정보와 함께 OK 응답을 반환
     */
    @GetMapping("/api/photos/{postIdx}")
    public ResponseEntity<Photo> getPhoto(@PathVariable("postIdx") Long postIdx) {
        log.debug("getPhoto 호출, postIdx : {}", postIdx);
        return ResponseEntity.ok(photoService.getPhoto(postIdx));
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
        log.debug("getEditPhoto 호출, postIdx : {}", postIdx);
        return ResponseEntity.ok(photoService.getEditPhoto(postIdx, (String) jwtTokenService.getClaim(accessToken, "id")));
    }

    /**
     * 사진 게시글 등록
     *
     * @param memberId 작성자의 회원 ID
     * @param photo    등록할 사진 게시글 정보
     * @return 생성된 위치 URI로 응답
     */
    @PostMapping("/api/photos")
    public ResponseEntity<Void> registerPhoto(@RequestAttribute("id") String memberId,
                                              @RequestPart(name = "photo") @Validated(PostValidationGroup.savePhoto.class) Photo photo,
                                              @RequestPart(name = "thumbnail", required = false) MultipartFile thumbnail,
                                              @RequestPart(name = "attachments", required = false) MultipartFile[] photos) {
        log.debug("registerPhoto 호출, memberId : {}, Community : {}, thumbnail : {}, photos : {}",
                memberId, photo, (thumbnail != null ? thumbnail : "null"), (photos != null ? photos.length : "null"));

        photoService.registerPhoto(photo.updateMemberId(memberId));
        attachmentService.saveAttachmentsWithThumbnail(moveFileToFront(thumbnail, photos), photo.getPostIdx(), AttachmentType.IMAGE);

        return ResponseEntity.created(URI.create("/api/photos/" + photo.getPostIdx())).build();
    }

    /**
     * 사진 게시글 수정
     *
     * @param postIdx                   게시글 번호
     * @param memberId                  회원 아이디
     * @param updatePhoto               삭제할 커뮤니티 게시글 정보
     * @param newAttachments            MultipartFile[] 객체
     * @param notDeletedIndexes 이전에 업로드된 파일 번호 목록
     * @return 생성된 위치 URI로 응답
     */
    @PutMapping("/api/photos/{postIdx}")
    public ResponseEntity<Void> updatePhoto(@PathVariable("postIdx") Long postIdx, @RequestAttribute("id") String memberId,
                                            @RequestPart(name = "updateOption", required = true) UpdateOption updateOption,
                                            @RequestPart(value = "photo") @Validated(PostValidationGroup.savePhoto.class) Photo updatePhoto,
                                            @RequestPart(name = "newThumbnail", required = false) MultipartFile newThumbnail,
                                            @RequestPart(name = "attachments", required = false) MultipartFile[] newAttachments,
                                            @RequestParam(name = "thumbnailAttachmentIdx", required = false) Long thumbnailAttachmentIdx,
                                            @RequestParam(name = "notDeletedIndexes", required = false) List<Long> notDeletedIndexes) {
        log.debug("updatePhoto 호출,  UpdateOption : {}, Update Photo : {}, newThumbnail : {},  newAttachments : {}, thumbnailIdx : {}, previouslyUploadedIndexes size : {}",
                updateOption, updatePhoto, (newThumbnail != null ? newThumbnail : "null"), (newAttachments != null ? newAttachments.length : "null"), thumbnailAttachmentIdx != null ? thumbnailAttachmentIdx : 0, (notDeletedIndexes != null ? notDeletedIndexes.size() : "null"));

        photoService.hasUpdateAccess(postIdx, memberId);

        photoService.updatePhoto(updatePhoto.updatePostIdx(postIdx).updateMemberId(memberId));
        attachmentService.updateAttachmentsWithVerifiedIndexes(updateOption, AddNewAttachments.of(newThumbnail, newAttachments),
                UpdateExistingAttachments.of(thumbnailAttachmentIdx, notDeletedIndexes), postIdx, AttachmentType.IMAGE);

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
                                            @RequestAttribute("id") String memberId) {
        log.debug("deleteCommunity 호출");

        photoService.hasDeleteAccess(postIdx, memberId);

        commentService.deleteAllByPostIdx(postIdx);
        attachmentService.deleteAllByPostIdxWithThumbNail(postIdx);
        photoService.deletePhoto(postIdx);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
