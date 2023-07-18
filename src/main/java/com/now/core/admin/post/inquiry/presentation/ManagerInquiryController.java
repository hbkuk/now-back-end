package com.now.core.admin.post.inquiry.presentation;

import com.now.core.admin.post.inquiry.application.ManagerInquiryService;
import com.now.core.authentication.constants.Authority;
import com.now.core.post.domain.Inquiry;
import com.now.core.post.domain.PostValidationGroup;
import com.now.core.post.presentation.dto.Answer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 문의 게시글 관련 작업을 위한 컨트롤러
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class ManagerInquiryController {

    private final ManagerInquiryService managerInquiryService;

    /**
     * 문의 게시글 응답
     */
    @GetMapping("/api/manager/inquiry/{postIdx}")
    public ResponseEntity<Inquiry> findInquiry(@PathVariable("postIdx") Long postIdx, @RequestAttribute("role") String authority) {
        log.debug("findInquiry 호출, postIdx : {}, authority : {}", postIdx, authority);
        return ResponseEntity.ok(managerInquiryService.findInquiry(postIdx, Authority.valueOf(authority)));
    }

    /**
     * 문의 게시글 답변 등록
     */
    @PostMapping("/api/manager/answer/{postIdx}")
    public ResponseEntity<Void> registerAnswer(@PathVariable("postIdx") Long postIdx,
                                               @RequestAttribute("id") String managerId, @RequestAttribute("role") String authority,
                                               @RequestBody @Validated({PostValidationGroup.saveAnswer.class}) Answer answer) {
        log.debug("registerInquiry 호출, memberId : {}, authority : {}, answer : {}", managerId, authority, answer);

        managerInquiryService.registerAnswer(answer.updateAnswerManagerId(managerId), Authority.valueOf(authority));

        return ResponseEntity.status(HttpStatus.CREATED).build(); // Status Code 201
    }
}
