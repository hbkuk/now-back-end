package com.now.core.post.notice.domain.repository;

import com.now.core.post.notice.domain.Notice;
import com.now.core.post.notice.domain.mapper.NoticeMapper;
import com.now.core.post.common.presentation.dto.Condition;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 공지 게시글 관련 정보를 관리하는 레포지토리
 */
@Repository
@RequiredArgsConstructor
public class NoticeRepository {

    private final NoticeMapper noticeMapper;

    /**
     * 상단에 고정된 공지 게시물과 조건에 맞는 게시물 정보를 조회 후 반환
     *
     * @param condition 게시물 제한 정보를 담은 객체
     * @return 공지사항 게시글 정보 리스트
     */
    public List<Notice> findAllNoticesWithPin(Condition condition) {
        return noticeMapper.findAllNoticesWithPin(condition);
    }


    /**
     * 공지 게시글 정보를 조회 후 반환
     *
     * @param postIdx 게시글 번호
     * @return 공지 게시글 정보
     */
    public Notice findNotice(Long postIdx) {
        return noticeMapper.findNotice(postIdx);
    }

    /**
     * 공지 게시글 등록
     *
     * @param notice 등록할 공지 게시글 정보
     */
    public void saveNotice(Notice notice) {
        noticeMapper.saveNotice(notice);
    }

    /**
     * 공지 게시글 수정
     *
     * @param notice 수정할 공지 게시글 정보
     */
    public void updateNotice(Notice notice) {
        noticeMapper.updateNotice(notice);
    }

    /**
     * 공지 게시글 삭제
     *
     * @param postIdx 삭제할 공지 게시글 번호
     */
    public void deleteNotice(Long postIdx) {
        noticeMapper.deleteNotice(postIdx);
    }
}
