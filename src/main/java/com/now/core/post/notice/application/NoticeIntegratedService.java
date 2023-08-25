package com.now.core.post.notice.application;

import com.now.core.post.common.application.PostService;
import com.now.core.post.notice.domain.Notice;
import com.now.core.post.common.presentation.dto.Condition;
import com.now.core.post.notice.presentation.dto.NoticesResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.now.common.config.CachingConfig.NOTICE_CACHE;
import static com.now.common.config.CachingConfig.POST_CACHE;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NoticeIntegratedService {

    private final PostService postService;
    private final NoticeService noticeService;

    /**
     * 조건에 따라 페이지 정보와 함께 모든 공지 게시글 목록 반환
     *
     * @param condition 조회 조건
     * @return 공지 게시글 목록과 페이지 정보
     */
    @Transactional(readOnly = true)
    @Cacheable(value = NOTICE_CACHE, key="#condition.hashCode()")
    public NoticesResponse getAllNoticesWithPageInfo(Condition condition) {
        return NoticesResponse.builder()
                .notices(noticeService.getAllNoticesWithPin(condition))
                .page(condition.getPage().calculatePageInfo(postService.getTotalPostCount(condition)))
                .build();
    }

    /**
     * 공지 게시글을 조회하고 조회수를 증가시킨 뒤 반환
     *
     * @param postIdx 게시글 번호
     * @return 조회된 공지 게시글
     */
    @CacheEvict(value = {POST_CACHE, NOTICE_CACHE}, allEntries = true)
    public Notice getNoticeAndIncrementViewCount(Long postIdx) {
        Notice notice = noticeService.getNotice(postIdx);
        postService.incrementViewCount(postIdx);
        return notice;
    }
}
