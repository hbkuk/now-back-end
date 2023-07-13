package com.now.core.post.application;

import com.now.core.category.domain.constants.PostGroup;
import com.now.core.post.domain.abstractions.ManagerPost;
import com.now.core.post.domain.abstractions.MemberPost;
import com.now.core.post.domain.Community;
import com.now.core.post.domain.Inquiry;
import com.now.core.post.domain.Notice;
import com.now.core.post.domain.Photo;
import com.now.core.manager.domain.Manager;
import com.now.core.manager.application.ManagerService;
import com.now.core.member.application.MemberService;
import com.now.core.member.domain.Member;
import com.now.core.post.exception.CannotWritePostException;
import com.now.core.post.exception.PermissionDeniedException;
import com.now.core.post.domain.PostRepository;
import com.now.core.authentication.constants.Authority;
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
    private final MemberService memberService;
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
     * 매니저가 작성한 게시글을 저장
     *
     * @param managerPost 매니저 게시글 객체
     * @param authority   권한
     * @throws PermissionDeniedException 권한이 없을 경우 발생하는 예외
     * @throws CannotWritePostException  게시글 작성이 불가능한 경우 발생하는 예외
     */
    public void registerManagerPost(ManagerPost managerPost, Authority authority) {
        if (authority != Authority.MANAGER) {
            throw new PermissionDeniedException(messageSource.getMessage("error.permission.denied"));
        }

        if (!PostGroup.isCategoryInGroup(managerPost.getCategory(), managerPost.getPostGroup())) {
            throw new CannotWritePostException(messageSource.getMessage("error.write.failed"));
        }

        Manager manager = managerService.findManagerById(managerPost.getManagerId());

        postRepository.saveManagerPost(managerPost.updateManagerIdx(manager.getManagerIdx()));
    }

    /**
     * 회원이 작성한 게시글을 저장
     *
     * @param memberPost 회원 게시글 객체
     * @param authority  권한
     * @throws PermissionDeniedException    권한이 없을 경우 발생하는 예외
     * @throws CannotWritePostException     게시글 작성이 불가능한 경우 발생하는 예외
     */
    public void registerMemberPost(MemberPost memberPost, Authority authority) {
        if (authority != Authority.MEMBER) {
            throw new PermissionDeniedException(messageSource.getMessage("error.permission.denied"));
        }

        if (!PostGroup.isCategoryInGroup(memberPost.getCategory(), memberPost.getPostGroup())) {
            throw new CannotWritePostException(messageSource.getMessage("error.write.failed"));
        }

        Member member = memberService.findMemberById(memberPost.getMemberId());

        postRepository.saveMemberPost(memberPost.updateMemberIdx(member.getMemberIdx()));
    }
}

