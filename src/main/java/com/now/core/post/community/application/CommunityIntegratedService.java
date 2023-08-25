package com.now.core.post.community.application;

import com.now.core.attachment.application.AttachmentService;
import com.now.core.attachment.domain.constants.AttachmentType;
import com.now.core.authentication.application.JwtTokenService;
import com.now.core.comment.application.CommentService;
import com.now.core.post.common.application.PostService;
import com.now.core.post.common.application.dto.AddNewAttachments;
import com.now.core.post.common.application.dto.UpdateExistingAttachments;
import com.now.core.post.community.domain.Community;
import com.now.core.post.community.presentation.dto.CommunitiesResponse;
import com.now.core.post.common.presentation.dto.Condition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CommunityIntegratedService {

    private final PostService postService;
    private final CommunityService communityService;
    private final AttachmentService attachmentService;
    private final CommentService commentService;
    private final JwtTokenService jwtTokenService;

    /**
     * 조건에 따라 페이지 정보와 함께 모든 커뮤니티 게시글 목록 반환
     *
     * @param condition 조회 조건
     * @return 커뮤니티 게시글 목록과 페이지 정보
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "communityCache", key="#condition.hashCode()")
    public CommunitiesResponse getAllCommunitiesWithPageInfo(Condition condition) {
        return CommunitiesResponse.builder()
                .communities(communityService.getAllCommunities(condition))
                .page(condition.getPage().calculatePageInfo(postService.getTotalPostCount(condition)))
                .build();
    }

    /**
     * 커뮤니티 게시글을 조회하고 조회수를 증가시킨 뒤 반환
     *
     * @param postIdx 게시글 번호
     * @return 조회된 커뮤니티 게시글
     */
    @CacheEvict(value = {"postCache", "communityCache"}, allEntries = true)
    public Community getCommunityAndIncrementViewCount(Long postIdx) {
        Community community = communityService.getCommunity(postIdx);
        postService.incrementViewCount(postIdx);
        return community;
    }

    /**
     * 액세스 토큰 확인 후 커뮤니티 게시글을 조회하여 반환
     *
     * @param postIdx     게시글 번호
     * @param accessToken 엑세스 토큰
     * @return 조회된 커뮤니티 게시글
     */
    @Transactional(readOnly = true)
    public Community getEditCommunity(Long postIdx, String accessToken) {
        return communityService.getEditCommunity(postIdx, (String) jwtTokenService.getClaim(accessToken, "id"));
    }

    /**
     * 커뮤니티 게시글과 함께 새로운 첨부 파일 등록
     *
     * @param community   커뮤니티 게시글
     * @param attachments 첨부 파일 배열
     */
    @CacheEvict(value = {"postCache", "communityCache"}, allEntries = true)
    public void registerCommunity(Community community, MultipartFile[] attachments) {
        communityService.registerCommunity(community);
        attachmentService.saveAttachments(attachments, community.getPostIdx(), AttachmentType.FILE);
    }

    /**
     * 커뮤니티 게시글 업데이트 후 첨부 파일 수정
     *
     * @param updatedCommunity          업데이트된 커뮤니티 게시글
     * @param addNewAttachments         새로 추가되는 첨부 파일
     * @param updateExistingAttachments 기존 첨부 파일 업데이트 정보
     */
    @CacheEvict(value = {"postCache", "communityCache"}, allEntries = true)
    public void updateCommunity(Community updatedCommunity,
                                AddNewAttachments addNewAttachments, UpdateExistingAttachments updateExistingAttachments) {
        communityService.hasUpdateAccess(updatedCommunity.getPostIdx(), updatedCommunity.getMemberId());

        communityService.updateCommunity(updatedCommunity);
        attachmentService.updateAttachments(addNewAttachments, updateExistingAttachments,
                updatedCommunity.getPostIdx(), AttachmentType.FILE);
    }

    /**
     * 커뮤니티 게시글 삭제 후 관련된 댓글 및 첨부 파일 삭제
     *
     * @param postIdx  게시글 번호
     * @param memberId 멤버 아이디
     */
    @CacheEvict(value = {"postCache", "communityCache"}, allEntries = true)
    public void deleteCommunity(Long postIdx, String memberId) {
        communityService.hasDeleteAccess(postIdx, memberId);

        postService.deleteAllPostReactionByPostIdx(postIdx);
        commentService.deleteAllByPostIdx(postIdx);
        attachmentService.deleteAllByPostIdx(postIdx);
        communityService.deleteCommunity(postIdx);
    }

}
