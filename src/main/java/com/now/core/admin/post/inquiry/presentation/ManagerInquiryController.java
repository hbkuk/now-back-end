package com.now.core.admin.post.inquiry.presentation;

import com.now.core.admin.post.inquiry.application.ManagerInquiryService;
import com.now.core.post.inquiry.domain.Inquiry;
import com.now.core.post.common.domain.constants.PostValidationGroup;
import com.now.core.post.inquiry.presentation.dto.Answer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

/**
 * 문의 게시글 관련 작업을 위한 컨트롤러
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class ManagerInquiryController {

    private final ManagerInquiryService managerInquiryService;

    /**
     * 문의 게시글 응답(비밀글 여부 X)
     *
     * @param postIdx 게시글 번호
     * @return 문의 게시글
     */
    @GetMapping("/api/manager/inquiries/{postIdx}")
    public ResponseEntity<Inquiry> getInquiry(@PathVariable("postIdx") Long postIdx) {
        return ResponseEntity.ok(managerInquiryService.getInquiry(postIdx));
    }

    /**
     * 문의 게시글 답변 등록
     */
    @PostMapping("/api/manager/answers/{postIdx}")
    public ResponseEntity<Void> registerAnswer(@PathVariable("postIdx") Long postIdx, @RequestAttribute("id") String managerId,
                                               @RequestBody @Validated({PostValidationGroup.saveAnswer.class}) Answer answer) {
        managerInquiryService.registerAnswer(answer.updatePostIdx(postIdx).updateAnswerManagerId(managerId));

        return ResponseEntity.created(URI.create("/api/answer/" + postIdx)).build();
    }
    
    // TODO: 답변 수정
}
