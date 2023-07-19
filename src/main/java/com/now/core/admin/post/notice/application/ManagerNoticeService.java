package com.now.core.admin.post.notice.application;

import com.now.common.exception.ErrorType;
import com.now.common.exception.ForbiddenException;
import com.now.core.admin.manager.domain.Manager;
import com.now.core.admin.manager.domain.ManagerRepository;
import com.now.core.admin.manager.exception.InvalidManagerException;
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
    private final ManagerRepository managerRepository;

    /**
     * 공지 게시글 등록
     *
     * @param notice    등록할 공지 게시글 정보
     */
    public void registerNotice(Notice notice) {
        Manager manager = getManager(notice.getManagerId());

        if (!PostGroup.isCategoryInGroup(PostGroup.NOTICE, notice.getCategory())) {
            throw new CannotCreatePostException(ErrorType.NOT_FOUND_CATEGORY);
        }

        postRepository.saveNotice((Notice) notice.updateManagerIdx(manager.getManagerIdx()));
    }


    // TODO: 매니저별 권한 부여 -> Notice 도메인 객체에서 canUpdate(Authority authority) 선언
    // TODO: 공지 수정 로그 기능 추가 -> 현재 ROOT 권한
    /**
     * 공지 게시글 수정
     * 
     * @param updatedNotice 수정할 공지 게시글 정보
     */
    public void updateNotice(Notice updatedNotice) {
        Manager manager = getManager(updatedNotice.getManagerId());

        Notice notice = getNotice(updatedNotice.getPostIdx());
        if(!notice.canUpdate(manager)) {
            throw new ForbiddenException(ErrorType.FORBIDDEN);
        }

        if (!PostGroup.isCategoryInGroup(PostGroup.NOTICE, updatedNotice.getCategory())) {
            throw new CannotCreatePostException(ErrorType.NOT_FOUND_CATEGORY);
        }

        postRepository.updateNotice(updatedNotice);
    }

    // TODO: 매니저별 권한 부여 -> Notice 도메인 객체에서 canDelete(Authority authority) 선언
    // TODO: 공지 수정 로그 기능 추가 -> 현재 Only, ROOT 권한
    /**
     * 공지 게시글 삭제
     *
     * @param postIdx   게시글 번호
     */
    public void deleteNotice(Long postIdx, String managerId) {
        Manager manager = getManager(managerId);

        Notice notice = getNotice(postIdx);
        if(!notice.canDelete(manager)) {
            throw new ForbiddenException(ErrorType.FORBIDDEN);
        }

        postRepository.deleteNotice(postIdx);
    }

    /**
     * 공지 게시글 응답
     *
     * @param postIdx 게시글 번호
     * @return 공지 게시글 정보
     */
    public Notice getNotice(Long postIdx) {
        Notice notice = postRepository.findNotice(postIdx);
        if (notice == null) {
            throw new InvalidPostException(ErrorType.NOT_FOUND_POST);
        }

        return notice;
    }

    /**
     * 매니저 객체 응답
     *
     * @param managerId 매니저 ID
     * @return 매니저 객체
     */
    private Manager getManager(String managerId) {
        Manager manager = managerRepository.findById(managerId);
        if(manager == null) {
            throw new InvalidManagerException(ErrorType.NOT_FOUND_MANAGER);
        }

        return manager;
    }
}

