package com.now.core.post.presentation;

import com.now.core.authentication.constants.Authority;
import com.now.core.attachment.application.AttachmentService;
import com.now.core.attachment.domain.constants.AttachmentType;
import com.now.core.post.domain.PostValidationGroup;
import com.now.core.post.application.PhotoService;
import com.now.core.post.domain.Photo;
import com.now.core.post.presentation.dto.Condition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

/**
 * 게시글 관련 작업을 위한 컨트롤러
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class PhotoController {

    private final PhotoService photoService;
    private final AttachmentService attachmentService;

    /**
     * 모든 사진 게시글 정보를 조회하는 핸들러 메서드
     *
     * @param condition 게시물 제한 정보를 담은 객체
     * @return 모든 사진 게시글 정보와 함께 OK 응답을 반환
     */
    @GetMapping("/api/photos")
    public ResponseEntity<List<Photo>> retrieveAllPhotos(@Valid @ModelAttribute Condition condition) {
        log.debug("retrieveAllPhotos 호출, condition : {}", condition);

        return new ResponseEntity<>(photoService.retrieveAllPhotos(condition), HttpStatus.OK);
    }

    /**
     * 사진 게시글 응답
     */
    @GetMapping("/api/photo/{postIdx}")
    public ResponseEntity<Photo> findPhotoByPostIdx(@PathVariable("postIdx") Long postIdx) {
        log.debug("findPhotoByPostIdx 호출, postIdx : {}", postIdx);
        return ResponseEntity.ok(photoService.findByPostIdx(postIdx));
    }

    /**
     * 사진 게시글 등록
     *
     * @param memberId 작성자의 회원 ID
     * @param photo  등록할 사진 게시글 정보
     * @return 생성된 게시글에 대한 CREATED 응답을 반환
     */
    @PostMapping("/api/photo")
    public ResponseEntity<Void> registerPhoto(@RequestAttribute("id") String memberId, @RequestAttribute("role") String authority,
                                              @RequestPart(value = "photo") @Validated(PostValidationGroup.savePhoto.class) Photo photo,
                                              @RequestPart(value = "attachment", required = false) MultipartFile[] multipartFiles) {
        log.debug("registerPhoto 호출, memberId : {}, authority : {}, Community : {}, Multipart : {}", memberId, authority, photo, (multipartFiles != null ? multipartFiles.length : "null"));

        photoService.registerPhoto(photo.updateMemberId(memberId), Authority.valueOf(authority));
        attachmentService.saveAttachmentsWithThumbnail(multipartFiles, photo.getPostIdx(), AttachmentType.IMAGE);

        return ResponseEntity.status(HttpStatus.CREATED).build(); // Status Code 201
    }
}
