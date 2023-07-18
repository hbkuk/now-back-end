package com.now.core.post.presentation;

import com.now.core.post.application.InquiryService;
import com.now.core.post.domain.Inquiry;
import com.now.core.post.domain.PostValidationGroup;
import com.now.core.post.presentation.dto.Condition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 문의 게시글 관련 작업을 위한 컨트롤러
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class InquiryController {

    private final InquiryService inquiryService;

    /**
     * 모든 문의 게시글 정보를 조회하는 핸들러 메서드
     *
     * @param condition 게시물 제한 정보를 담은 객체
     * @return 모든 문의 게시글 정보와 함께 OK 응답을 반환
     */
    @GetMapping("/api/inquiries")
    public ResponseEntity<List<Inquiry>> retrieveAllInquiries(@Valid @ModelAttribute Condition condition) {
        log.debug("retrieveAllInquiries 호출, condition : {}", condition);

        return new ResponseEntity<>(inquiryService.retrieveAllInquiries(condition), HttpStatus.OK);
    }

    /**
     * 문의 게시글 응답
     */
    @GetMapping("/api/inquiry/{postIdx}")
    public ResponseEntity<Inquiry> findInquiry(@PathVariable("postIdx") Long postIdx,
                                               @RequestHeader(name = HttpHeaders.AUTHORIZATION, required = false) final String token) {
        log.debug("findInquiry 호출, postIdx : {}, token : {}", postIdx, token);
        return ResponseEntity.ok(inquiryService.findInquiry(postIdx, token));
    }

    /**
     * 문의 게시글 등록
     *
     * @param memberId 작성자의 회원 ID
     * @param inquiry  등록할 문의 게시글 정보
     * @return 생성된 게시글에 대한 CREATED 응답을 반환
     */
    @PostMapping("/api/inquiry")
    public ResponseEntity<Void> registerInquiry(@RequestAttribute("id") String memberId,
                                                @RequestBody @Validated({PostValidationGroup.saveInquiry.class}) Inquiry inquiry) {
        log.debug("registerInquiry 호출, memberId : {}, inquiry : {}", memberId, inquiry);

        inquiryService.registerInquiry(inquiry.updateMemberId(memberId));

        return ResponseEntity.status(HttpStatus.CREATED).build(); // Status Code 201
    }

    /**
     * 문의 게시글 수정
     *
     * @param memberId 작성자의 회원 ID
     * @param inquiry  등록할 문의 게시글 정보
     * @return 수정된 게시글에 대한 CREATED 응답을 반환
     */
    @PutMapping("/api/inquiry/{postIdx}")
    public ResponseEntity<Void> updateInquiry(@PathVariable("postIdx") Long postIdx,
                                              @RequestAttribute("id") String memberId,
                                              @RequestBody @Validated({PostValidationGroup.saveInquiry.class}) Inquiry inquiry) {
        log.debug("updateInquiry 호출, memberId : {}, inquiry : {}", memberId, inquiry);

        inquiryService.hasUpdateAccess(postIdx, memberId);
        inquiryService.updateInquiry(inquiry.updateMemberId(memberId));
        return ResponseEntity.status(HttpStatus.CREATED).build(); // Status Code 201
    }

    /**
     * 문의 게시글 삭제
     *
     * @param postIdx 게시글 번호
     * @return 응답 결과
     */
    @DeleteMapping("/api/inquiry/{postIdx}")
    public ResponseEntity<Void> deleteInquiry(@PathVariable("postIdx") Long postIdx,
                                              @RequestAttribute("id") String memberId) {
        log.debug("deleteInquiry 호출");

        inquiryService.hasDeleteAccess(postIdx, memberId);
        inquiryService.deleteInquiry(postIdx, memberId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
