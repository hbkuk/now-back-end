package com.now.core.post.presentation;

import com.now.core.authentication.application.JwtTokenService;
import com.now.core.authentication.presentation.AuthenticationPrincipal;
import com.now.core.post.application.CommunityService;
import com.now.core.post.application.PostService;
import com.now.core.post.application.dto.AddNewAttachments;
import com.now.core.post.application.dto.UpdateExistingAttachments;
import com.now.core.post.application.integrated.CommunityIntegratedService;
import com.now.core.post.domain.Community;
import com.now.core.post.domain.constants.PostValidationGroup;
import com.now.core.post.presentation.dto.CommunitiesResponse;
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

/**
 * 커뮤니티 게시글 관련 작업을 위한 컨트롤러
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class CommunityController {

    private final CommunityIntegratedService postManagementService;
    private final PostService postService;
    private final JwtTokenService jwtTokenService;
    private final CommunityService communityService;

    /**
     * 모든 커뮤니티 게시글 정보를 조회
     *
     * @param condition 게시물 제한 정보를 담은 객체
     * @return 모든 커뮤니티 게시글 정보와 함께 OK 응답을 반환
     */
    @GetMapping("/api/communities")
    public ResponseEntity<CommunitiesResponse> getAllCommunities(@Valid Condition condition) {
        CommunitiesResponse communitiesResponse = CommunitiesResponse.builder()
                .communities(communityService.getAllCommunities(condition.updatePage()))
                .page(condition.getPage().calculatePaginationInfo(postService.getTotalPostCount(condition)))
                .build();

        return new ResponseEntity<>(communitiesResponse, HttpStatus.OK);
    }

    /**
     * 커뮤니티 게시글 응답
     *
     * @param postIdx 게시글 번호
     * @return 커뮤니티 게시글 정보
     */
    @GetMapping("/api/communities/{postIdx}")
    public ResponseEntity<Community> getCommunity(@PathVariable("postIdx") Long postIdx) {
        return ResponseEntity.ok(communityService.getCommunity(postIdx));
    }

    /**
     * 수정 커뮤니티 게시글 응답
     *
     * @param postIdx 게시글 번호
     * @return 커뮤니티 게시글 정보
    ion in registerCommunity")).when(communityService).registerCommunity(community);  */
    @GetMapping("/api/communities/{postIdx}/edit")
    public ResponseEntity<Community> getEditCommunity(@PathVariable("postIdx") Long postIdx,
                                                      @CookieValue(value = JwtTokenService.ACCESS_TOKEN_KEY, required = true) String accessToken) {
        return ResponseEntity.ok(communityService.getEditCommunity(postIdx, (String) jwtTokenService.getClaim(accessToken, "id")));
    }

    /**
     * 커뮤니티 게시글 등록
     *
     * @param memberId    회원 아이디
     * @param community   등록할 커뮤니티 게시글 정보
     * @param attachments MultipartFile[] 객체
     * @return 생성된 위치 URI로 응답
     */
    @PostMapping("/api/communities")
    public ResponseEntity<Void> registerCommunity(@AuthenticationPrincipal String memberId,
                                                  @RequestPart(name = "community") @Validated(PostValidationGroup.saveCommunity.class) Community community,
                                                  @RequestPart(name = "attachments", required = false) MultipartFile[] attachments) {

        postManagementService.registerCommunity(community.updateMemberId(memberId), attachments);
        return ResponseEntity.created(URI.create("/api/communities/" + community.getPostIdx())).build();
    }

    /**
     * 커뮤니티 게시글 수정
     *
     * @param postIdx           게시글 번호
     * @param memberId          회원 아이디
     * @param updatedCommunity  수정할 커뮤니티 게시글 정보
     * @param attachments       MultipartFile[] 객체
     * @param notDeletedIndexes 삭제하지 않을 파일 번호 목록
     * @return 생성된 위치 URI로 응답
     */
    @PutMapping("/api/communities/{postIdx}")
    public ResponseEntity<Void> updateCommunity(@PathVariable("postIdx") Long postIdx,
                                                @AuthenticationPrincipal String memberId,
                                                @Validated(PostValidationGroup.saveNotice.class) @RequestPart(name = "community") Community updatedCommunity,
                                                @RequestPart(name = "attachments", required = false) MultipartFile[] attachments,
                                                @RequestParam(name = "notDeletedIndexes", required = false) List<Long> notDeletedIndexes) {

        postManagementService.updateCommunity(updatedCommunity.updatePostIdx(postIdx).updateMemberId(memberId),
                AddNewAttachments.of(null, attachments),
                UpdateExistingAttachments.of(null, notDeletedIndexes));

        return ResponseEntity.created(URI.create("/api/communities/" + updatedCommunity.getPostIdx())).build();
    }

    /**
     * 커뮤니티 게시글 삭제
     *
     * @param postIdx  게시글 번호
     * @param memberId 회원 아이디
     * @return 응답 본문이 없는 상태 코드 204 반환
     */
    @DeleteMapping("/api/communities/{postIdx}")
    public ResponseEntity<Void> deleteCommunity(@PathVariable("postIdx") Long postIdx,
                                                @AuthenticationPrincipal String memberId) {

        postManagementService.deleteCommunity(postIdx, memberId);
        return ResponseEntity.noContent().build();
    }
}
