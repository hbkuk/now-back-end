package com.now.core.post.presentation;

import com.now.core.authentication.constants.Authority;
import com.now.core.attachment.application.AttachmentService;
import com.now.core.attachment.domain.constants.AttachmentType;
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
    private final AttachmentService fileService;

    /**
     * 모든 커뮤니티 게시글 정보를 조회하는 핸들러 메서드
     *
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
     * 커뮤니티 게시글을 등록
     *
     * @param memberId    작성자의 회원 ID
     * @param community 등록할 커뮤니티 게시글 정보
     * @return 생성된 게시글에 대한 CREATED 응답을 반환
     */
    @PostMapping("/api/community")
    public ResponseEntity<Void> registerCommunity(@RequestAttribute("id") String memberId, @RequestAttribute("role") String authority,
                                                  @RequestPart(value = "community") @Validated(PostValidationGroup.saveCommunity.class) Community community,
                                                  @RequestPart(value = "file", required = false) MultipartFile[] multipartFiles) {
        log.debug("registerCommunity 호출, memberId : {}, authority : {}, Community : {}, Multipart : {}", memberId, authority, community, (multipartFiles != null ? multipartFiles.length : "null"));

        communityService.registerCommunity(community.updateMemberId(memberId), Authority.valueOf(authority));
        fileService.saveAttachments(multipartFiles, community.getPostIdx(), AttachmentType.FILE);

        return ResponseEntity.status(HttpStatus.CREATED).build(); // Status Code 201
    }
}
