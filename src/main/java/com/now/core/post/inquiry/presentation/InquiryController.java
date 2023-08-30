package com.now.core.post.inquiry.presentation;

import com.now.core.authentication.application.JwtTokenService;
import com.now.core.authentication.presentation.AuthenticationPrincipal;
import com.now.core.post.inquiry.application.InquiryIntegratedService;
import com.now.core.post.inquiry.domain.Inquiry;
import com.now.core.post.common.domain.constants.PostValidationGroup;
import com.now.core.post.common.presentation.dto.Condition;
import com.now.core.post.inquiry.presentation.dto.InquiriesResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

/**
 * 문의 게시글 관련 작업을 위한 컨트롤러
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class InquiryController {

    private final InquiryIntegratedService inquiryIntegratedService;

    /**
     * 모든 문의 게시글 정보를 조회하는 핸들러 메서드
     *
     * @param condition 게시물 제한 정보를 담은 객체
     * @return 모든 문의 게시글 정보와 함께 OK 응답을 반환
     */
    @GetMapping("/api/inquiries")
    public ResponseEntity<InquiriesResponse> getAllInquiries(@Valid Condition condition) {
        return new ResponseEntity<>(
                inquiryIntegratedService.getAllInquiriesWithPageInfo(condition.updatePage()), HttpStatus.OK);
    }

    /**
     * 공개 문의 게시글 조회
     *
     * @param postIdx 게시글 번호
     * @return 문의 게시글 정보와 함께 OK 응답을 반환
     */
    @GetMapping("/api/inquiries/{postIdx}")
    public ResponseEntity<Inquiry> getPublicInquiry(@PathVariable("postIdx") Long postIdx) {
        return ResponseEntity.ok(inquiryIntegratedService.getPublicInquiryAndIncrementViewCount(postIdx));
    }

    /**
     * 비공개 설정된 문의 게시글 조회
     *
     * @param postIdx     게시글 번호
     * @param password    비밀글로 설정된 비밀번호
     * @param accessToken 액세스 토큰
     * @return 문의 게시글 정보와 함께 OK 응답을 반환
     */
    @PostMapping("/api/inquiries/secret/{postIdx}")
    public ResponseEntity<Inquiry> getPrivateInquiry(@PathVariable("postIdx") Long postIdx,
                                                    @RequestParam(required = false) String password,
                                                    @CookieValue(value = JwtTokenService.ACCESS_TOKEN_KEY, required = false) String accessToken) {
        return ResponseEntity.ok(inquiryIntegratedService.getPrivateInquiryAndIncrementViewCount(postIdx, accessToken, password));
    }

    /**
     * 수정 문의 게시글 조회
     *
     * @param postIdx 게시글 번호
     * @param accessToken 액세스 토큰
     * @return 문의 게시글 정보와 함께 OK 응답을 반환
     */
    @GetMapping("/api/inquiries/{postIdx}/edit")
    public ResponseEntity<Inquiry> getEditInquiry(@PathVariable("postIdx") Long postIdx,
                                                  @CookieValue(value = JwtTokenService.ACCESS_TOKEN_KEY, required = true) String accessToken) {
        return ResponseEntity.ok(inquiryIntegratedService.getEditInquiry(postIdx, accessToken));
    }

    /**
     * 문의 게시글 등록
     *
     * @param memberId 작성자의 회원 ID
     * @param inquiry  등록할 문의 게시글 정보
     * @return 생성된 게시글에 대한 CREATED 응답을 반환
     */
    @PostMapping("/api/inquiries")
    public ResponseEntity<Void> registerInquiry(@AuthenticationPrincipal String memberId,
                                                @RequestPart(name = "inquiry") @Validated({PostValidationGroup.saveInquiry.class}) Inquiry inquiry) {
        inquiryIntegratedService.registerInquiry(inquiry.updateMemberId(memberId));
        return ResponseEntity.created(URI.create("/api/inquiries/" + inquiry.getPostIdx())).build();
    }

    /**
     * 문의 게시글 수정
     *
     * @param memberId 작성자의 회원 ID
     * @param updateInquiry  등록할 문의 게시글 정보
     * @return 수정된 게시글에 대한 CREATED 응답을 반환
     */
    @PutMapping("/api/inquiries/{postIdx}")
    public ResponseEntity<Void> updateInquiry(@PathVariable("postIdx") Long postIdx,
                                              @AuthenticationPrincipal String memberId,
                                              @RequestPart(name = "inquiry") @Validated({PostValidationGroup.saveInquiry.class}) Inquiry updateInquiry) {
        inquiryIntegratedService.updateInquiry(updateInquiry.updateMemberId(memberId).updatePostIdx(postIdx));
        return ResponseEntity.created(URI.create("/api/inquiries/" + updateInquiry.getPostIdx())).build();
    }

    /**
     * 문의 게시글 삭제
     *
     * @param postIdx 게시글 번호
     * @return 응답 결과
     */
    @DeleteMapping("/api/inquiries/{postIdx}")
    public ResponseEntity<Void> deleteInquiry(@PathVariable("postIdx") Long postIdx,
                                              @AuthenticationPrincipal String memberId) {
        inquiryIntegratedService.deleteInquiry(postIdx, memberId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
