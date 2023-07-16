package com.now.core.post.presentation;

import com.now.core.attachment.application.AttachmentService;
import com.now.core.attachment.domain.constants.AttachmentType;
import com.now.core.authentication.constants.Authority;
import com.now.core.comment.application.CommentService;
import com.now.core.post.application.CommunityService;
import com.now.core.post.domain.Community;
import com.now.core.post.domain.PostValidationGroup;
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
 * 커뮤니티 게시글 관련 작업을 위한 컨트롤러
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class CommunityController {

    private final CommunityService communityService;
    private final AttachmentService attachmentService;
    private final CommentService commentService;

    /**
     * 모든 커뮤니티 게시글 정보를 조회
     *
     * @param condition 게시물 제한 정보를 담은 객체
     * @return 모든 커뮤니티 게시글 정보와 함께 OK 응답을 반환
     */
    @GetMapping("/api/communities")
    public ResponseEntity<List<Community>> retrieveCommunities(@Valid @ModelAttribute Condition condition) {
        log.debug("retrieveCommunities 호출, condition : {}", condition);

        return new ResponseEntity<>(communityService.retrieveAllCommunities(condition), HttpStatus.OK);
    }

    /**
     * 커뮤니티 게시글 응답
     *
     * @param postIdx 게시글 번호
     * @return 공지 게시글 정보
     */
    @GetMapping("/api/community/{postIdx}")
    public ResponseEntity<Community> findCommunityByPostIdx(@PathVariable("postIdx") Long postIdx) {
        log.debug("findCommunityByPostIdx 호출, postIdx : {}", postIdx);
        return ResponseEntity.ok(communityService.findByPostIdx(postIdx));
    }

    /**
     * 커뮤니티 게시글 등록
     *
     * @param memberId       회원 아이디
     * @param community      등록할 커뮤니티 게시글 정보
     * @param multipartFiles MultipartFile[] 객체
     * @return 생성된 게시글에 대한 CREATED 응답을 반환
     */
    @PostMapping("/api/community")
    public ResponseEntity<Void> registerCommunity(@RequestAttribute("id") String memberId,
                                                  @RequestPart(value = "community") @Validated(PostValidationGroup.saveCommunity.class) Community community,
                                                  @RequestPart(value = "attachment", required = false) MultipartFile[] multipartFiles) {
        log.debug("registerCommunity 호출, memberId : {}, Community : {}, Multipart : {}", memberId, community, (multipartFiles != null ? multipartFiles.length : "null"));

        communityService.registerCommunity(community.updateMemberId(memberId));
        attachmentService.saveAttachments(multipartFiles, community.getPostIdx(), AttachmentType.FILE);

        return ResponseEntity.status(HttpStatus.CREATED).build(); // Status Code 201
    }

    /**
     * 커뮤니티 게시글 수정
     *
     * @param postIdx                   게시글 번호
     * @param memberId                  회원 아이디
     * @param updatedCommunity          수정할 커뮤니티 게시글 정보
     * @param multipartFiles            MultipartFile[] 객체
     * @param previouslyUploadedIndexes 이전에 업로드된 파일 번호 목록
     * @return 수정된 게시글에 대한 CREATED 응답을 반환
     */
    @PutMapping("/api/community/{postIdx}")
    public ResponseEntity<Void> updateCommunity(@PathVariable("postIdx") Long postIdx, @RequestAttribute("id") String memberId, @RequestAttribute("role") String authority,
                                                @Validated(PostValidationGroup.saveNotice.class) @RequestPart(value = "community") Community updatedCommunity,
                                                @RequestPart(value = "attachment", required = false) MultipartFile[] multipartFiles,
                                                @RequestParam(value = "attachmentIdx", required = false) List<Long> previouslyUploadedIndexes) {
        log.debug("updateCommunity 호출,  Update Community : {}, Multipart Files Size: {}, previouslyUploadedIndexes size : {}",
                updatedCommunity, (multipartFiles != null ? multipartFiles.length : "null"), (previouslyUploadedIndexes != null ? previouslyUploadedIndexes.size() : "null"));

        communityService.hasUpdateAccess(postIdx, memberId, Authority.valueOf(authority));

        communityService.updateCommunity(updatedCommunity.updatePostIdx(postIdx).updateMemberId(memberId));
        attachmentService.updateAttachments(multipartFiles, previouslyUploadedIndexes, postIdx, AttachmentType.FILE);

        return ResponseEntity.status(HttpStatus.CREATED).build(); // Status Code 201
    }

    /**
     * 커뮤니티 게시글 삭제
     *
     * @param postIdx 게시글 번호
     * @param memberId 회원 아이디
     * @param authority 권한
     * @return No Content 응답
     */
    @DeleteMapping("/api/community/{postIdx}")
    public ResponseEntity<Void> deleteCommunity(@PathVariable("postIdx") Long postIdx,
                                                @RequestAttribute("id") String memberId, @RequestAttribute("role") String authority) {
        log.debug("deleteCommunity 호출");

        communityService.hasDeleteAccess(postIdx, memberId, Authority.valueOf(authority));

        commentService.deleteAllByPostIdx(postIdx);
        attachmentService.deleteAllByPostIdx(postIdx);
        communityService.deleteCommunity(postIdx);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
