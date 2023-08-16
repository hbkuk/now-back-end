package com.now.core.post.domain.mapper;

import com.now.core.post.domain.Notice;
import com.now.core.post.presentation.dto.Condition;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 공지 게시글 정보에 접근하는 매퍼 인터페이스
 */
@Mapper
public interface NoticeMapper {


    /**
     * 모든 공지사항 게시글 정보를 조회 후 반환
     *
     * @param condition 게시물 제한 정보를 담은 객체
     * @return 공지사항 게시글 정보 리스트
     */
    List<Notice> findAllNotices(Condition condition);


    /**
     * 상단에 고정된 공지 게시물과 조건에 맞는 게시물 정보를 조회 후 반환
     *
     * @param condition 게시물 제한 정보를 담은 객체
     * @return 공지사항 게시글 정보 리스트
     */
    List<Notice> findAllNoticesWithPin(Condition condition);


    /**
     * 공지 게시글 정보를 조회 후 반환
     *
     * @param postIdx 게시글 번호
     * @return 공지 게시글 정보
     */
    Notice findNotice(Long postIdx);


    /**
     * 공지 게시글 등록
     *
     * @param notice 등록할 공지 게시글 정보
     */
    void saveNotice(Notice notice);


    /**
     * 공지 게시글 수정
     *
     * @param notice 수정할 공지 게시글 정보
     */
    void updateNotice(Notice notice);


    /**
     * 공지 게시글 삭제
     *
     * @param postIdx 게시글 번호
     */
    void deleteNotice(Long postIdx);
}
