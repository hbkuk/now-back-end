package com.now.mapper;

import com.now.domain.post.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 게시글 정보에 접근하는 매퍼 인터페이스
 *
 * @Mapper
 * : MyBatis의 매퍼 인터페이스임을 나타냄.
 */
@Mapper
public interface PostMapper {

    /**
     * 모든 공지사항 게시글 정보를 조회 후 반환
     *
     * @return 공지사항 게시글 정보 리스트
     */
    List<Notice> findAllNotices();

    /**
     * 모든 커뮤니티 게시글 정보를 조회 후 반환
     *
     * @return 커뮤니티 게시글 정보 리스트
     */
    List<Community> findAllCommunity();

    /**
     * 모든 사진 게시글 정보를 조회 후 반환
     *
     * @return 사진 게시글 정보 리스트
     */
    List<Photo> findAllPhotos();

    /**
     * 모든 문의 게시글 정보를 조회 후 반환
     *
     * @return 문의 게시글 정보 리스트
     */
    List<Inquiry> findAllInquiries();

    /**
     * 게시글 등록
     *
     * @param post 등록할 게시글 정보
     */
    void insert(Post post);
}
