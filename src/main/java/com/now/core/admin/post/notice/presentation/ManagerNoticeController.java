package com.now.core.admin.post.notice.presentation;

import com.now.core.admin.post.notice.application.ManagerNoticeService;
import com.now.core.authentication.presentation.AuthenticationPrincipal;
import com.now.core.post.notice.domain.Notice;
import com.now.core.post.common.domain.constants.PostValidationGroup;
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
     *
     * @param managerId 매니저 ID
     * @param notice    등록할 공지 게시글 정보
     * @return 생성된 위치 URI로 응답
     */
    @PostMapping("/api/manager/notices")
    public ResponseEntity<Void> registerNotice(@AuthenticationPrincipal String managerId,
                                               @RequestPart(name = "notice") @Validated(PostValidationGroup.saveNotice.class) Notice notice) {
        managerNoticeService.registerNotice(notice.updateManagerId(managerId));
        return ResponseEntity.created(URI.create("/api/notice/" + notice.getPostIdx())).build();
    }

    /**
     * 수정 공지 게시글 응답
     *
     * @param postIdx 게시글 번호
     * @return 공지 게시글 정보
     */
    @GetMapping("/api/manager/notices/{postIdx}/edit")
    public ResponseEntity<Notice> getEditNotice(@PathVariable("postIdx") Long postIdx, @AuthenticationPrincipal String managerId) {
        return ResponseEntity.ok(managerNoticeService.getEditNotice(postIdx, managerId));
    }


    /**
     * 공지 게시글 수정
     *
     * @param postIdx       게시글 번호
     * @param managerId     매니저 ID
     * @param updatedNotice 수정된 공지 게시글 정보
     * @return 수정된 게시글에 대한 CREATED 응답을 반환
     */
    @PutMapping("/api/manager/notices/{postIdx}")
    public ResponseEntity<Void> updateNotice(@PathVariable("postIdx") Long postIdx, @AuthenticationPrincipal String managerId,
                                             @RequestPart(name = "notice") @Validated(PostValidationGroup.saveNotice.class) Notice updatedNotice) {
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
    @DeleteMapping("/api/manager/notices/{postIdx}")
    public ResponseEntity<Void> deleteNotice(@PathVariable("postIdx") Long postIdx, @AuthenticationPrincipal String managerId) {
        managerNoticeService.deleteNotice(postIdx, managerId);
        return ResponseEntity.noContent().build();
    }
}
