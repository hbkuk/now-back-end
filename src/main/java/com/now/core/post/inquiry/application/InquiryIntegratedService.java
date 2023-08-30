package com.now.core.post.inquiry.application;

import com.now.common.exception.ErrorType;
import com.now.core.authentication.application.JwtTokenService;
import com.now.core.comment.application.CommentService;
import com.now.core.post.common.application.PostService;
import com.now.core.post.common.exception.CannotCreatePostException;
import com.now.core.post.common.presentation.dto.Condition;
import com.now.core.post.inquiry.domain.Inquiry;
import com.now.core.post.inquiry.domain.constants.PrivacyUpdateOption;
import com.now.core.post.inquiry.presentation.dto.InquiriesResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.now.common.config.CachingConfig.INQUIRY_CACHE;
import static com.now.common.config.CachingConfig.POST_CACHE;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class InquiryIntegratedService {

    private final PostService postService;
    private final InquiryService inquiryService;
    private final CommentService commentService;
    private final JwtTokenService jwtTokenService;

    /**
     * 조건에 따라 페이지 정보와 함께 모든 문의 게시글 목록 반환
     *
     * @param condition 조회 조건
     * @return 문의 게시글 목록과 페이지 정보
     */
    @Transactional(readOnly = true)
    @Cacheable(value = INQUIRY_CACHE, key = "#condition.hashCode()")
    public InquiriesResponse getAllInquiriesWithPageInfo(Condition condition) {
        return InquiriesResponse.builder()
                .inquiries(inquiryService.getAllInquiries(condition.updatePage()))
                .page(condition.getPage().calculatePageInfo(postService.getTotalPostCount(condition)))
                .build();
    }

    /**
     * 공개 문의 게시글을 조회하고 조회수를 증가시킨 뒤 반환
     *
     * @param postIdx 게시글 번호
     * @return 조회된 문의 게시글
     */
    @CacheEvict(value = {POST_CACHE, INQUIRY_CACHE}, allEntries = true)
    public Inquiry getPublicInquiryAndIncrementViewCount(Long postIdx) {
        Inquiry inquiry = inquiryService.getPublicInquiry(postIdx);
        postService.incrementViewCount(postIdx);
        return inquiry;
    }

    /**
     * 비공개 문의 게시글을 조회하고 조회수를 증가시킨 뒤 반환
     *
     * @param postIdx     게시글 번호
     * @param accessToken 액세스 토큰
     * @param password    비밀번호
     * @return 조회된 문의 게시글
     */
    @CacheEvict(value = {POST_CACHE, INQUIRY_CACHE}, allEntries = true)
    public Inquiry getPrivateInquiryAndIncrementViewCount(Long postIdx, String accessToken, String password) {
        String memberId = null;
        if (accessToken != null) {
            memberId = (String) jwtTokenService.getClaim(accessToken, "id");
        }
        Inquiry inquiry = inquiryService.getPrivateInquiry(postIdx, memberId, password);
        postService.incrementViewCount(postIdx);
        return inquiry;
    }


    /**
     * 액세스 토큰 확인 후 문의 게시글을 조회하여 반환
     *
     * @param postIdx     게시글 번호
     * @param accessToken 엑세스 토큰
     * @return 조회된 문의 게시글
     */
    @Transactional(readOnly = true)
    public Inquiry getEditInquiry(Long postIdx, String accessToken) {
        return inquiryService.getEditInquiry(postIdx, (String) jwtTokenService.getClaim(accessToken, "id"));
    }

    /**
     * 문의 게시글 등록
     *
     * @param inquiry 문의 게시글
     */
    @CacheEvict(value = {POST_CACHE, INQUIRY_CACHE}, allEntries = true)
    public void registerInquiry(Inquiry inquiry) {
        if (inquiry.isSecretInquiryWithoutPassword()) {
            throw new CannotCreatePostException(ErrorType.INVALID_SECRET);
        }
        inquiryService.registerInquiry(inquiry);
    }

    /**
     * 문의 게시글 수정
     *
     * @param updatedInquiry 업데이트된 문의 게시글
     */
    @CacheEvict(value = {POST_CACHE, INQUIRY_CACHE}, allEntries = true)
    public void updateInquiry(Inquiry updatedInquiry, PrivacyUpdateOption privacyUpdateOption) {
        inquiryService.hasUpdateAccess(updatedInquiry);
        inquiryService.updateInquiry(updatedInquiry, privacyUpdateOption);
    }

    /**
     * 문의 게시글 삭제
     *
     * @param postIdx  게시글 번호
     * @param memberId 회원 아이디
     */
    @CacheEvict(value = {POST_CACHE, INQUIRY_CACHE}, allEntries = true)
    public void deleteInquiry(Long postIdx, String memberId) {
        inquiryService.hasDeleteAccess(postIdx, memberId);

        postService.deleteAllPostReactionByPostIdx(postIdx);
        commentService.deleteAllByPostIdx(postIdx);
        inquiryService.deleteInquiry(postIdx, memberId);
    }
}
