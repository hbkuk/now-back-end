package com.now.core.admin.post.inquiry.application;

import com.now.common.exception.ErrorType;
import com.now.common.exception.ForbiddenException;
import com.now.core.admin.manager.application.ManagerService;
import com.now.core.admin.manager.domain.Manager;
import com.now.core.authentication.constants.Authority;
import com.now.core.post.domain.Inquiry;
import com.now.core.post.domain.PostRepository;
import com.now.core.post.exception.InvalidPostException;
import com.now.core.post.presentation.dto.Answer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 문의 게시글 관련 비즈니스 로직을 처리하는 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ManagerInquiryService {

    private final PostRepository postRepository;
    private final ManagerService managerService;

    /**
     * 문의 게시글 응답
     *
     * @param postIdx 게시글 번호
     * @return 문의 게시글 정보
     */
    public Inquiry findInquiry(Long postIdx, Authority authority) {
        if (authority != Authority.MANAGER) {
            throw new ForbiddenException(ErrorType.FORBIDDEN);
        }

        Inquiry inquiry = postRepository.findInquiry(postIdx);
        if (inquiry == null) {
            throw new InvalidPostException(ErrorType.NOT_FOUND_POST);
        }
        return inquiry;
    }

    /**
     * 문의 게시글의 답변 등록
     *
     * @param answer    등록할 답변 정보
     * @param authority 권한
     */
    public void registerAnswer(Answer answer, Authority authority) {
        if (authority != Authority.MANAGER) {
            throw new ForbiddenException(ErrorType.FORBIDDEN);
        }

        Manager manager = managerService.findManagerById(answer.getAnswerManagerId());

        postRepository.saveAnswer(answer.updateAnswerManaegrIdx(manager.getManagerIdx()));
    }
}

