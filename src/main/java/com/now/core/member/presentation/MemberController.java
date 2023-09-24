package com.now.core.member.presentation;

import com.now.core.authentication.presentation.AuthenticationPrincipal;
import com.now.core.member.application.MemberService;
import com.now.core.member.domain.Member;
import com.now.core.member.domain.MemberValidationGroup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 회원 관련 작업을 위한 컨트롤러
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    /**
     * 회원 정보를 등록하는 핸들러 메서드
     *
     * @param member 등록할 회원 정보
     * @return ResponseEntity 객체 (HTTP 응답)
     */
    @PostMapping("/api/sign-up")
    public ResponseEntity<Void> signUp(@RequestBody @Validated(MemberValidationGroup.signup.class) Member member) {
        memberService.registerMember(member);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 회원 정보를 수정하는 핸들러 메서드
     *
     * @param memberId 수정할 회원 아이디
     * @param member 수정할 회원 정보
     * @return ResponseEntity 객체 (HTTP 응답)
     */
    @PutMapping("/api/members")
    public ResponseEntity<Void> update(@AuthenticationPrincipal String memberId,
                                       @RequestBody @Validated(MemberValidationGroup.update.class) Member member) {
        memberService.updateMember(member.updateMemberId(memberId));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
