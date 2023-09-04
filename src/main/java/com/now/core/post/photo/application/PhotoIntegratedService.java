package com.now.core.post.photo.application;

import com.now.core.attachment.application.AttachmentService;
import com.now.core.attachment.domain.constants.AttachmentType;
import com.now.core.authentication.application.JwtTokenService;
import com.now.core.comment.application.CommentService;
import com.now.core.post.common.application.PostService;
import com.now.core.post.common.application.dto.AddNewAttachments;
import com.now.core.post.common.application.dto.UpdateExistingAttachments;
import com.now.core.post.photo.domain.Photo;
import com.now.core.post.common.domain.constants.UpdateOption;
import com.now.core.post.common.presentation.dto.Condition;
import com.now.core.post.photo.presentation.dto.PhotosResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.now.common.config.CachingConfig.PHOTO_CACHE;
import static com.now.common.config.CachingConfig.POST_CACHE;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PhotoIntegratedService {

    private final PostService postService;
    private final PhotoService photoService;
    private final AttachmentService attachmentService;
    private final CommentService commentService;
    private final JwtTokenService jwtTokenService;

    /**
     * 조건에 따라 페이지 정보와 함께 모든 사진 게시글 목록 반환
     *
     * @param condition 조회 조건
     * @return 사진 게시글 목록과 페이지 정보
     */
    @Transactional(readOnly = true)
    @Cacheable(value = PHOTO_CACHE, key="#condition.hashCode()")
    public PhotosResponse getAllPhotosWithPageInfo(Condition condition) {
        return PhotosResponse.builder()
                .photos(photoService.getAllPhotos(condition))
                .page(condition.getPage().calculatePageInfo(postService.getTotalPostCount(condition)))
                .build();
    }

    /**
     * 사진 게시글을 조회하고 조회수를 증가시킨 뒤 반환
     *
     * @param postIdx 게시글 번호
     * @return 조회된 사진 게시글
     */
    public Photo getPhotoAndIncrementViewCount(Long postIdx) {
        Photo photo = photoService.getPhoto(postIdx);
        postService.incrementViewCount(postIdx);
        return photo;
    }

    /**
     * 액세스 토큰 확인 후 사진 게시글을 조회하여 반환
     *
     * @param postIdx     게시글 번호
     * @param accessToken 엑세스 토큰
     * @return 조회된 사진 게시글
     */
    @Transactional(readOnly = true)
    public Photo getEditPhoto(Long postIdx, String accessToken) {
        return photoService.getEditPhoto(postIdx,
                (String) jwtTokenService.getClaim(accessToken, "id"));
    }

    /**
     * 사진 게시글과 함께 새로운 첨부 파일 등록
     *
     * @param photo             사진 게시글
     * @param addNewAttachments 새로운 첨부파일
     */
    @CacheEvict(value = {POST_CACHE, PHOTO_CACHE}, allEntries = true)
    public void registerPhoto(Photo photo, AddNewAttachments addNewAttachments) {
        photoService.registerPhoto(photo);
        attachmentService.saveAttachmentsWithThumbnail(
                addNewAttachments, photo.getPostIdx(), AttachmentType.IMAGE);
    }

    /**
     * 사진 게시글 업데이트 후 첨부 파일 수정
     *
     * @param updatePhoto               업데이트된 사진 게시글
     * @param updateOption              수정 옵션
     * @param addNewAttachments         새로 추가되는 첨부 파일
     * @param updateExistingAttachments 기존 첨부 파일 업데이트 정보
     */
    @CacheEvict(value = {POST_CACHE, PHOTO_CACHE}, allEntries = true)
    public void updatePhoto(Photo updatePhoto, UpdateOption updateOption,
                            AddNewAttachments addNewAttachments, UpdateExistingAttachments updateExistingAttachments) {

        photoService.hasUpdateAccess(updatePhoto.getPostIdx(), updatePhoto.getMemberId());

        photoService.updatePhoto(updatePhoto);
        attachmentService.updateAttachmentsWithVerifiedIndexes(updateOption, addNewAttachments,
                updateExistingAttachments, updatePhoto.getPostIdx(), AttachmentType.IMAGE);
    }

    /**
     * 사진 게시글 삭제 후 관련된 댓글 및 첨부 파일 삭제
     *
     * @param postIdx  게시글 번호
     * @param memberId 멤버 아이디
     */
    @CacheEvict(value = {POST_CACHE, PHOTO_CACHE}, allEntries = true)
    public void deletePhoto(Long postIdx, String memberId) {
        photoService.hasDeleteAccess(postIdx, memberId);

        postService.deleteAllPostReactionByPostIdx(postIdx);
        commentService.deleteAllByPostIdx(postIdx);
        attachmentService.deleteAllByPostIdxWithThumbNail(postIdx);
        photoService.deletePhoto(postIdx);
    }
}
