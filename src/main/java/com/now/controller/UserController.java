package com.now.controller;

import com.now.domain.user.User;
import com.now.security.JwtTokenService;
import com.now.service.UserService;
import com.now.validation.UserValidationGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 사용자 관련 작업을 위한 컨트롤러
 */
@Slf4j
@RestController
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 사용자 정보를 등록하는 핸들러 메서드
     *
     * @param user 등록할 사용자 정보
     * @return ResponseEntity 객체 (HTTP 응답)
     */
    @PostMapping("/api/signup")
    public ResponseEntity registerUser(@Validated(UserValidationGroup.signup.class) @RequestBody User user) {
        log.debug("/api/signup, registerUser 핸들러 메서드 호출, User : {}", user);

        userService.registerUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).build(); // Status Code 201
    }

    /**
     * 사용자 정보를 조회 후 로그인 처리하는 핸들러 메서드
     *
     * @param user 조회할 사용자 정보
     * @return ResponseEntity 객체 (HTTP 응답)
     */
    @PostMapping("/api/login")
    public ResponseEntity<HttpHeaders> loginUser(@Validated(UserValidationGroup.login.class) @RequestBody User user) {
        log.debug("/api/login, loginUser 핸들러 메서드 호출, User : {}", user);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", userService.generateAuthToken(user));

        return ResponseEntity.ok().headers(httpHeaders).build();
    }
}
