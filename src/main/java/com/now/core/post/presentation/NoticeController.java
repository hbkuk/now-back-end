package com.now.core.post.presentation;

import com.now.core.authentication.constants.Authority;
import com.now.core.post.application.NoticeService;
import com.now.core.post.domain.Notice;
import com.now.core.post.domain.PostValidationGroup;
import com.now.core.post.presentation.dto.Condition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 공지 게시글 관련 작업을 위한 컨트롤러
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    /**
     * 모든 공지 게시물 정보를 조회하는 핸들러 메서드
     *
     * @param condition 게시물 제한 정보를 담은 객체
     * @return 모든 공지 게시글 정보와 함께 OK 응답을 반환
     */
    @GetMapping("/api/notices")
    public ResponseEntity<List<Notice>> retrieveAllNotices(@Valid @ModelAttribute Condition condition) {
        log.debug("retrieveAllNotices 호출, condition : {}", condition);

        return new ResponseEntity<>(noticeService.retrieveAllNotices(condition), HttpStatus.OK);
    }

    /**
     * 공지 게시글 응답
     *
     * @param postIdx 게시글 번호
     * @return 공지 게시글 정보
     */
    @GetMapping("/api/notice/{postIdx}")
    public ResponseEntity<Notice> findNoticeByPostIdx(@PathVariable("postIdx") Long postIdx) {
        log.debug("findNoticeByPostIdx 호출, postIdx : {}", postIdx);

        return ResponseEntity.ok(noticeService.findByPostIdx(postIdx));
    }

    /**
     * 공지 게시글 등록
     * @param managerId 작성자의 매니저 ID
     * @param authority 권한
     * @param notice 등록할 공지 게시글 정보
     * @return 생성된 게시글에 대한 CREATED 응답을 반환
     */
    @PostMapping("/api/notice")
    public ResponseEntity<Void> registerNotice(@RequestAttribute("id") String managerId, @RequestAttribute("role") String authority,
                                               @RequestBody @Validated(PostValidationGroup.saveNotice.class) Notice notice) {
        log.debug("registerNotice 호출, managerId : {}, authority : {}, notice : {}", managerId, authority, notice);

        noticeService.registerNotice(notice.updateManagerId(managerId), Authority.valueOf(authority));
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
    @PutMapping("/api/notice/{postIdx}")
    public ResponseEntity<Void> updateNotice(@PathVariable("postIdx") Long postIdx,
                                             @RequestAttribute("role") String authority,
                                             @RequestBody @Validated(PostValidationGroup.saveNotice.class) Notice updatedNotice) {
        log.debug("updateNotice 호출,  Updated Notice : {}", updatedNotice);

        noticeService.updateNotice(updatedNotice.updatePostIdx(postIdx), Authority.valueOf(authority));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 공지 게시글 삭제
     *
     * @param postIdx   게시글 번호
     * @param authority 권한
     * @return 응답 결과
     */
    @DeleteMapping("/api/notice/{postIdx}")
    public ResponseEntity<Void> deleteNotice(@PathVariable("postIdx") Long postIdx,
                                             @RequestAttribute("role") String authority) {
        log.debug("deleteNotice 호출");

        noticeService.deleteNotice(postIdx, Authority.valueOf(authority));
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
