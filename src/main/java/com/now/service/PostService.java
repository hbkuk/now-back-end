package com.now.service;

import com.now.domain.post.*;
import com.now.domain.user.User;
import com.now.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 게시글 관련 비즈니스 로직을 처리하는 서비스
 */
@Service
public class PostService {

    private PostRepository postRepository;
    private UserService userService;

    @Autowired
    public PostService(PostRepository postRepository, UserService userService) {
        this.postRepository = postRepository;
        this.userService = userService;
    }

    /**
     * 모든 공지사항 게시글 정보를 조회 후 반환
     *
     * @return 공지사항 게시글 정보 리스트
     */
    public List<Notice> retrieveAllNotices() {
        return postRepository.findAllNotices();
    }

    /**
     * 모든 커뮤니티 게시글 정보를 조회 후 반환
     *
     * @return 커뮤니티 게시글 정보 리스트
     */
    public List<Community> retrieveAllCommunity() {
        return postRepository.findAllCommunity();
    }

    /**
     * 모든 사진 게시글 정보를 조회 후 반환
     *
     * @return 사진 게시글 정보 리스트
     */
    public List<Photo> retrieveAllPhotos() {
        return postRepository.findAllPhotos();
    }

    /**
     * 모든 문의 게시글 정보를 조회 후 반환
     *
     * @return 문의 게시글 정보 리스트
     */
    public List<Inquiry> retrieveAllInquiries() {
        return postRepository.findAllInquries();
    }

    /**
     * 사용자 확인 후 게시글 등록
     *
     * @param post 등록할 게시글 정보
     */
    public void registerPost(Post post) {
        User user = userService.findUserById(post.getAuthorId());

        postRepository.insert(post.updateAuthorIdx(user.getUserIdx()));
    }
}

