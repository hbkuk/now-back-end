package com.now.core.admin.post.inquiry.application;

import com.now.common.exception.ErrorType;
import com.now.core.admin.manager.domain.Manager;
import com.now.core.admin.manager.domain.ManagerRepository;
import com.now.core.admin.manager.exception.InvalidManagerException;
import com.now.core.post.inquiry.domain.Inquiry;
import com.now.core.post.inquiry.domain.repository.InquiryRepository;
import com.now.core.post.common.exception.InvalidPostException;
import com.now.core.post.inquiry.presentation.dto.Answer;
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

    private final InquiryRepository inquiryRepository;
    private final ManagerRepository managerRepository;

    /**
     * 문의 게시글 응답
     *
     * @param postIdx 게시글 번호
     * @return 문의 게시글 정보
     */
    public Inquiry getInquiry(Long postIdx) {
        Inquiry inquiry = inquiryRepository.findInquiry(postIdx);
        if (inquiry == null) {
            throw new InvalidPostException(ErrorType.NOT_FOUND_POST);
        }

        return inquiry;
    }

    /**
     * 문의 게시글의 답변 등록
     *
     * @param answer    등록할 답변 정보
     */
    public void registerAnswer(Answer answer) {
        Manager manager = managerRepository.findById(answer.getAnswerManagerId());
        if(manager == null) {
            throw new InvalidManagerException(ErrorType.NOT_FOUND_MANAGER);
        }

        inquiryRepository.saveAnswer(answer.updateAnswerManagerIdx(manager.getManagerIdx()));
    }
}

