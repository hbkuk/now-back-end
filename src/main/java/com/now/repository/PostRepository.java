package com.now.repository;

import com.now.domain.post.*;
import com.now.mapper.PostMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 게시글 관련 정보를 관리하는 리포지토리
 */
@Repository
public class PostRepository {

    public PostMapper postMapper;

    @Autowired
    public PostRepository(PostMapper postMapper) {
        this.postMapper = postMapper;
    }


    /**
     * 모든 공지사항 게시글 정보를 조회 후 반환
     *
     * @return 공지사항 게시글 정보 리스트
     */
    public List<Notice> findAllNotices() {
        return postMapper.findAllNotices();
    }

    /**
     * 모든 커뮤니티 게시글 정보를 조회 후 반환
     *
     * @return 커뮤니티 게시글 정보 리스트
     */
    public List<Community> findAllCommunity() {
        return postMapper.findAllCommunity();
    }

    /**
     * 모든 사진 게시글 정보를 조회 후 반환
     *
     * @return 사진 게시글 정보 리스트
     */
    public List<Photo> findAllPhotos() {
        return postMapper.findAllPhotos();
    }

    /**
     * 모든 문의 게시글 정보를 조회 후 반환
     *
     * @return 문의 게시글 정보 리스트
     */
    public List<Inquiry> findAllInquiries() {
        return postMapper.findAllInquiries();
    }

    /**
     * 사용자로부터 게시글 등록
     *
     * @param post 등록할 게시글 정보
     */
    public void insertPostByUser(Post post) {
        postMapper.insertPostByUser(post);
    }

    /**
     * 매니저로부터 게시글 등록
     *
     * @param post 등록할 게시글 정보
     */
    public void insertPostByManager(Post post) {
        postMapper.insertPostByManager(post);
    }
}
