package com.now.service;

import com.now.domain.manager.Manager;
import com.now.domain.post.*;
import com.now.domain.user.User;
import com.now.exception.PermissionDeniedException;
import com.now.repository.PostRepository;
import com.now.security.Authority;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 게시글 관련 비즈니스 로직을 처리하는 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserService userService;
    private final ManagerService managerService;
    private final MessageSourceAccessor messageSource;

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
        return postRepository.findAllInquiries();
    }

    /**
     * 커뮤니티 게시글 등록
     *
     * @param post      등록할 게시글 정보
     * @param authority 권한 정보
     * @throws PermissionDeniedException 게시글을 작성할 권한이 없는 경우 발생하는 예외
     */
    public void registerCommunityPost(Post post, Authority authority) {
        User user = userService.findUserById(post.getAuthorId());

        if(authority != Authority.USER) {
            throw new PermissionDeniedException(messageSource.getMessage("error.permission.denied"));
        }

        postRepository.insert(post.updateAuthorIdx(user.getUserIdx()));
    }

    /**
     * 공지 게시글 등록
     *
     * @param post      등록할 게시글 정보
     * @param authority 권한 정보
     * @throws PermissionDeniedException 게시글을 작성할 권한이 없는 경우 발생하는 예외
     */
    public void registerNoticePost(Post post, Authority authority) {
        Manager manager = managerService.findManagerById(post.getAuthorId());

        if(authority != Authority.MANAGER) {
            throw new PermissionDeniedException(messageSource.getMessage("error.permission.denied"));
        }

        postRepository.insert(post.updateAuthorIdx(manager.getManagerIdx()));
    }
}

