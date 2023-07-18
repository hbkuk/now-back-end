package com.now.core.admin.post.notice.application;

import com.now.common.exception.ErrorType;
import com.now.common.exception.ForbiddenException;
import com.now.core.admin.manager.application.ManagerService;
import com.now.core.admin.manager.domain.Manager;
import com.now.core.authentication.constants.Authority;
import com.now.core.category.domain.constants.PostGroup;
import com.now.core.post.domain.Notice;
import com.now.core.post.domain.PostRepository;
import com.now.core.post.exception.CannotCreatePostException;
import com.now.core.post.exception.InvalidPostException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 공지 게시글 관련 비즈니스 로직을 처리하는 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ManagerNoticeService {

    private final PostRepository postRepository;
    private final ManagerService managerService;

    /**
     * 공지 게시글 응답
     *
     * @param postIdx 게시글 번호
     * @return 공지 게시글 정보
     */
    public Notice findByPostIdx(Long postIdx) {
        Notice notice = postRepository.findNotice(postIdx);
        if (notice == null) {
            throw new InvalidPostException(ErrorType.NOT_FOUND_POST);
        }

        return notice;
    }

    /**
     * 공지 게시글 등록
     *
     * @param notice    등록할 공지 게시글 정보
     * @param authority 권한
     */
    public void registerNotice(Notice notice, Authority authority) {
        if (!Authority.isManager(authority)) {
            throw new ForbiddenException(ErrorType.FORBIDDEN);
        }

        if (!PostGroup.isCategoryInGroup(PostGroup.NOTICE, notice.getCategory())) {
            throw new CannotCreatePostException(ErrorType.INVALID_CATEGORY);
        }

        Manager manager = managerService.findManagerById(notice.getManagerId());

        postRepository.saveNotice(notice.updateManagerIdx(manager.getManagerIdx()));
    }

    // TODO: 매니저별 권한 부여 -> Notice 도메인 객체에서 canUpdate(Authority authority) 선언
    // TODO: 공지 수정 로그 기능 추가 -> 현재 ROOT 권한
    /**
     * 공지 게시글 수정
     * @param updatedNotice 수정할 공지 게시글 정보
     * @param authority 권한
     */
    public void updateNotice(Notice updatedNotice, Authority authority) {
        Notice notice = findByPostIdx(updatedNotice.getPostIdx());

        if(!notice.canUpdate(authority)) {
            throw new ForbiddenException(ErrorType.FORBIDDEN);
        }

        if (!Authority.isManager(authority)) {
            throw new ForbiddenException(ErrorType.FORBIDDEN);
        }

        if (!PostGroup.isCategoryInGroup(PostGroup.NOTICE, updatedNotice.getCategory())) {
            throw new CannotCreatePostException(ErrorType.INVALID_CATEGORY);
        }

        postRepository.updateNotice(updatedNotice);
    }

    // TODO: 매니저별 권한 부여 -> Notice 도메인 객체에서 canDelete(Authority authority) 선언
    // TODO: 공지 수정 로그 기능 추가 -> 현재 Only, ROOT 권한
    /**
     * 공지 게시글 삭제
     *
     * @param postIdx   게시글 번호
     * @param authority 권한
     */
    public void deleteNotice(Long postIdx, Authority authority) {
        Notice notice = findByPostIdx(postIdx);

        if(!notice.canDelete(authority)) {
            throw new ForbiddenException(ErrorType.FORBIDDEN);
        }

        if (!Authority.isManager(authority)) {
            throw new ForbiddenException(ErrorType.FORBIDDEN);
        }

        postRepository.deleteNotice(postIdx);
    }
}

