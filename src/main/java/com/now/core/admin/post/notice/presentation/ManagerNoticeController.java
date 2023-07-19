package com.now.core.admin.post.notice.presentation;

import com.now.core.admin.post.notice.application.ManagerNoticeService;
import com.now.core.post.domain.Notice;
import com.now.core.post.domain.PostValidationGroup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

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
     * @param managerId 매니저 ID
     * @param notice 등록할 공지 게시글 정보
     * @return 생성된 위치 URI로 응답
     */
    @PostMapping("/api/manager/notice")
    public ResponseEntity<Void> registerNotice(@RequestAttribute("id") String managerId,
                                               @RequestBody @Validated(PostValidationGroup.saveNotice.class) Notice notice) {
        log.debug("registerNotice 호출, managerId : {}, notice : {}", managerId, notice);

        managerNoticeService.registerNotice(notice.updateManagerId(managerId));
        return ResponseEntity.created(URI.create("/api/notice/" + notice.getPostIdx())).build();
    }

    /**
     * 공지 게시글 수정
     *
     * @param postIdx       게시글 번호
     * @param managerId 매니저 ID
     * @param updatedNotice 수정된 공지 게시글 정보
     * @return 수정된 게시글에 대한 CREATED 응답을 반환
     */
    @PutMapping("/api/manager/notice/{postIdx}")
    public ResponseEntity<Void> updateNotice(@PathVariable("postIdx") Long postIdx,
                                             @RequestAttribute("id") String managerId,
                                             @RequestBody @Validated(PostValidationGroup.saveNotice.class) Notice updatedNotice) {
        log.debug("updateNotice 호출,  Updated Notice : {}", updatedNotice);

        managerNoticeService.updateNotice(updatedNotice.updatePostIdx(postIdx).updateManagerId(managerId));
        return ResponseEntity.created(URI.create("/api/notice/" + updatedNotice.getPostIdx())).build();
    }

    /**
     * 공지 게시글 삭제
     *
     * @param postIdx   게시글 번호
     * @param managerId 매니저 ID
     * @return 응답 본문이 없는 상태 코드 204 반환
     */
    @DeleteMapping("/api/manager/notice/{postIdx}")
    public ResponseEntity<Void> deleteNotice(@PathVariable("postIdx") Long postIdx,
                                             @RequestAttribute("id") String managerId) {
        log.debug("deleteNotice 호출");

        managerNoticeService.deleteNotice(postIdx, managerId);
        return ResponseEntity.noContent().build();
    }
}
