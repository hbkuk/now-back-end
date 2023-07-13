package com.now.core.manager.presentation;

import com.now.core.manager.domain.Manager;
import com.now.core.manager.application.ManagerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 매니저 관련 작업을 위한 컨트롤러
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class ManagerController {

    private final ManagerService managerService;

    /**
     * 매니저 정보를 조회 후 로그인 처리하는 핸들러 메서드
     *
     * @param manager 조회할 매니저 정보
     * @return ResponseEntity 객체 (HTTP 응답)
     */
    @PostMapping("/api/manager/login")
    public ResponseEntity<HttpHeaders> loginManager(@RequestBody Manager manager) {
        log.debug("loginManager 핸들러 메서드 호출, Manager : {}", manager);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", managerService.generateAuthToken(manager));

        return ResponseEntity.ok().headers(httpHeaders).build();
    }
}
