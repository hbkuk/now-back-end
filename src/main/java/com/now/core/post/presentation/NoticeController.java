package com.now.core.post.presentation;

import com.now.core.post.application.NoticeService;
import com.now.core.post.domain.Notice;
import com.now.core.post.presentation.dto.Condition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

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

        return new ResponseEntity<>(noticeService.getAllNotices(condition), HttpStatus.OK);
    }

    /**
     * 공지 게시글 응답
     *
     * @param postIdx 게시글 번호
     * @return 공지 게시글 정보
     */
    @GetMapping("/api/notices/{postIdx}")
    public ResponseEntity<Notice> getNotice(@PathVariable("postIdx") Long postIdx) {
        log.debug("getNotice 호출, postIdx : {}", postIdx);

        return ResponseEntity.ok(noticeService.getNotice(postIdx));
    }
}
