package com.now.core.admin.manager.presentation;

import com.now.core.admin.manager.application.ManagerService;
import com.now.core.admin.manager.domain.Manager;
import com.now.core.authentication.application.JwtTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 매니저 관련 작업을 위한 컨트롤러
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class ManagerController {

    private final ManagerService managerService;

    /**
     * 매니저 여부를 확인하는 핸들러 메서드
     *
     * @param managerId 확인할 매니저 ID
     * @return 매니저이면 true, 아니면 false
     */
    @GetMapping("/api/manager/isManager")
    public ResponseEntity<Void> isManager(@RequestAttribute("id") String managerId) {
        log.debug("isManager 핸들러 메서드 호출, ManagerId : {}", managerId);

        managerService.findManagerById(managerId);

        return ResponseEntity.ok().build();
    }

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
