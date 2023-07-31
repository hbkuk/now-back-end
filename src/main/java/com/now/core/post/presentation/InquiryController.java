package com.now.core.post.presentation;

import com.now.common.exception.ErrorType;
import com.now.core.authentication.application.JwtTokenService;
import com.now.core.post.application.InquiryService;
import com.now.core.post.domain.Inquiry;
import com.now.core.post.domain.PostValidationGroup;
import com.now.core.post.exception.CannotCreatePostException;
import com.now.core.post.presentation.dto.Condition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

/**
 * 문의 게시글 관련 작업을 위한 컨트롤러
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class InquiryController {

    private final InquiryService inquiryService;
    private final JwtTokenService jwtTokenService;

    /**
     * 모든 문의 게시글 정보를 조회하는 핸들러 메서드
     *
     * @param condition 게시물 제한 정보를 담은 객체
     * @return 모든 문의 게시글 정보와 함께 OK 응답을 반환
     */
    @GetMapping("/api/inquiries")
    public ResponseEntity<List<Inquiry>> getAllInquiries(@Valid @ModelAttribute Condition condition) {
        log.debug("getAllInquiries 호출, condition : {}", condition);

        return new ResponseEntity<>(inquiryService.getAllInquiries(condition), HttpStatus.OK);
    }

    /**
     * 문의 게시글 조회
     *
     * @param postIdx 게시글 번호
     * @return 문의 게시글 정보와 함께 OK 응답을 반환
     */
    @GetMapping("/api/inquiries/{postIdx}")
    public ResponseEntity<Inquiry> getInquiry(@PathVariable("postIdx") Long postIdx) {
        log.debug("getInquiry 호출, postIdx : {}", postIdx);

        return ResponseEntity.ok(inquiryService.getInquiryWithSecretCheck(postIdx));
    }

    /**
     * 비밀글 설정된 문의 게시글 조회
     *
     * @param postIdx     게시글 번호
     * @param password    비밀글로 설정된 비밀번호
     * @param accessToken 액세스 토큰
     * @return 문의 게시글 정보와 함께 OK 응답을 반환
     */
    @PostMapping("/api/inquiries/secret/{postIdx}")
    public ResponseEntity<Inquiry> getSecretInquiry(@PathVariable("postIdx") Long postIdx,
                                                    @RequestParam(required = false) String password,
                                                    @CookieValue(value = JwtTokenService.ACCESS_TOKEN_KEY, required = false) String accessToken) {
        log.debug("getSecretInquiry 호출, postIdx : {}, password : {}, token : {}", postIdx, password, accessToken);

        String memberId = null;
        if (accessToken != null) {
            memberId = (String) jwtTokenService.getClaim(accessToken, "id");
        }

        return ResponseEntity.ok(inquiryService.getInquiryWithSecretCheck(postIdx, memberId, password));
    }

    /**
     * 수정 문의 게시글 조회
     * @param postIdx 게시글 번호
     * @param accessToken 액세스 토큰
     * @return 문의 게시글 정보와 함께 OK 응답을 반환
     */
    @GetMapping("/api/inquiries/{postIdx}/edit")
    public ResponseEntity<Inquiry> getEditInquiry(@PathVariable("postIdx") Long postIdx,
                                                  @CookieValue(value = JwtTokenService.ACCESS_TOKEN_KEY, required = true) String accessToken) {
        log.debug("getEditInquiry 호출, postIdx : {}", postIdx);

        return ResponseEntity.ok(inquiryService.getEditInquiry(postIdx, (String) jwtTokenService.getClaim(accessToken, "id")));
    }

    /**
     * 문의 게시글 등록
     *
     * @param memberId 작성자의 회원 ID
     * @param inquiry  등록할 문의 게시글 정보
     * @return 생성된 게시글에 대한 CREATED 응답을 반환
     */
    @PostMapping("/api/inquiries")
    public ResponseEntity<Void> registerInquiry(@RequestAttribute("id") String memberId,
                                                @RequestBody @Validated({PostValidationGroup.saveInquiry.class}) Inquiry inquiry) {
        log.debug("registerInquiry 호출, memberId : {}, inquiry : {}", memberId, inquiry);

        if (inquiry.isSecretInquiryWithoutPassword()) {
            throw new CannotCreatePostException(ErrorType.INVALID_SECRET);
        }

        inquiryService.registerInquiry(inquiry.updateMemberId(memberId));
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
                                              @RequestAttribute("id") String memberId,
                                              @RequestBody @Validated({PostValidationGroup.saveInquiry.class}) Inquiry updateInquiry) {
        log.debug("updateInquiry 호출, memberId : {}, inquiry : {}", memberId, updateInquiry);

        inquiryService.hasUpdateAccess(postIdx, memberId);

        inquiryService.updateInquiry(updateInquiry.updateMemberId(memberId));
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
                                              @RequestAttribute("id") String memberId) {
        log.debug("deleteInquiry 호출");

        inquiryService.hasDeleteAccess(postIdx, memberId);

        inquiryService.deleteInquiry(postIdx, memberId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
