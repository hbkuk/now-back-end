package com.now.core.member.presentation;

import com.now.core.member.application.MemberService;
import com.now.core.member.domain.Member;
import com.now.core.member.domain.MemberValidationGroup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
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
    @PostMapping("/api/signup")
    public ResponseEntity<Void> registerMember(@RequestBody
                                               @Validated(MemberValidationGroup.signup.class) Member member) {
        log.debug("registerMember 핸들러 메서드 호출, Member : {}", member);

        memberService.registerMember(member);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 회원 정보를 조회 후 로그인 처리하는 핸들러 메서드
     *
     * @param member 조회할 회원 정보
     * @return ResponseEntity 객체 (HTTP 응답)
     */
    @PostMapping("/api/login")
    public ResponseEntity<HttpHeaders> loginMember(@RequestBody Member member) {
        log.debug("loginMember 핸들러 메서드 호출, Member : {}", member);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", memberService.generateAuthToken(member));

        return ResponseEntity.ok().headers(httpHeaders).build();
    }
}
