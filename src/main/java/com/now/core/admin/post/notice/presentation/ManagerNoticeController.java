package com.now.core.admin.post.notice.presentation;

import com.now.core.admin.post.notice.application.ManagerNoticeService;
import com.now.core.authentication.constants.Authority;
import com.now.core.post.domain.Notice;
import com.now.core.post.domain.PostValidationGroup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 공지 게시글 관련 작업을 위한 컨트롤러
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class ManagerNoticeController {

    private final ManagerNoticeService managerNoticeService;


    /**
     * 공지 게시글 등록
     * @param managerId 작성자의 매니저 ID
     * @param authority 권한
     * @param notice 등록할 공지 게시글 정보
     * @return 생성된 게시글에 대한 CREATED 응답을 반환
     */
    @PostMapping("/api/manager/notice")
    public ResponseEntity<Void> registerNotice(@RequestAttribute("id") String managerId, @RequestAttribute("role") String authority,
                                               @RequestBody @Validated(PostValidationGroup.saveNotice.class) Notice notice) {
        log.debug("registerNotice 호출, managerId : {}, authority : {}, notice : {}", managerId, authority, notice);

        managerNoticeService.registerNotice(notice.updateManagerId(managerId), Authority.valueOf(authority));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    /**
     * 공지 게시글 수정
     *
     * @param postIdx       게시글 번호
     * @param authority     권한
     * @param updatedNotice 수정된 공지 게시글 정보
     * @return 수정된 게시글에 대한 CREATED 응답을 반환
     */
    @PutMapping("/api/manager/notice/{postIdx}")
    public ResponseEntity<Void> updateNotice(@PathVariable("postIdx") Long postIdx, @RequestAttribute("role") String authority,
                                             @RequestBody @Validated(PostValidationGroup.saveNotice.class) Notice updatedNotice) {
        log.debug("updateNotice 호출,  Updated Notice : {}", updatedNotice);

        managerNoticeService.updateNotice(updatedNotice.updatePostIdx(postIdx), Authority.valueOf(authority));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 공지 게시글 삭제
     *
     * @param postIdx   게시글 번호
     * @param authority 권한
     * @return 응답 결과
     */
    @DeleteMapping("/api/manager/notice/{postIdx}")
    public ResponseEntity<Void> deleteNotice(@PathVariable("postIdx") Long postIdx,
                                             @RequestAttribute("role") String authority) {
        log.debug("deleteNotice 호출");

        managerNoticeService.deleteNotice(postIdx, Authority.valueOf(authority));
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
