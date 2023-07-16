package com.now.core.post.application;

import com.now.core.authentication.constants.Authority;
import com.now.core.category.domain.constants.PostGroup;
import com.now.core.manager.application.ManagerService;
import com.now.core.manager.domain.Manager;
import com.now.core.post.domain.Notice;
import com.now.core.post.domain.PostRepository;
import com.now.core.post.exception.CannotWritePostException;
import com.now.core.post.exception.PermissionDeniedException;
import com.now.core.post.presentation.dto.Condition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * 공지 게시글 관련 비즈니스 로직을 처리하는 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NoticeService {

    private final PostRepository postRepository;
    private final ManagerService managerService;
    private final MessageSourceAccessor messageSource;

    /**
     * 모든 공지 게시글 정보를 조회 후 반환
     *
     * @param condition 게시물 제한 정보를 담은 객체
     * @return 공지사항 게시글 정보 리스트
     */
    public List<Notice> retrieveAllNotices(Condition condition) {
        return postRepository.findAllNotices(condition);
    }

    /**
     * 공지 게시글 응답
     *
     * @param postIdx 게시글 번호
     * @return 공지 게시글 정보
     */
    public Notice findByPostIdx(Long postIdx) {
        Notice notice = postRepository.findNotice(postIdx);
        if (notice == null) {
            throw new NoSuchElementException(messageSource.getMessage("error.noSuch.post"));
        }

        return notice;
    }

    /**
     * 공지 게시글 등록
     *
     * @param notice    등록할 공지 게시글 정보
     * @param authority 권한
     * @throws PermissionDeniedException 권한이 없을 경우 발생하는 예외
     * @throws CannotWritePostException  게시글 작성이 불가능한 경우 발생하는 예외
     */
    public void registerNotice(Notice notice, Authority authority) {
        if (!Authority.isManager(authority)) {
            throw new PermissionDeniedException(messageSource.getMessage("error.permission.denied"));
        }

        if (!PostGroup.isCategoryInGroup(PostGroup.NOTICE, notice.getCategory())) {
            throw new CannotWritePostException(messageSource.getMessage("error.write.failed"));
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
        if (Authority.isManager(authority)) {
            throw new PermissionDeniedException(messageSource.getMessage("error.permission.denied"));
        }

        if (!PostGroup.isCategoryInGroup(PostGroup.NOTICE, updatedNotice.getCategory())) {
            throw new CannotWritePostException(messageSource.getMessage("error.write.failed"));
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
        if (Authority.isManager(authority)) {
            throw new PermissionDeniedException(messageSource.getMessage("error.permission.denied"));
        }

        postRepository.deleteNotice(postIdx);
    }
}

