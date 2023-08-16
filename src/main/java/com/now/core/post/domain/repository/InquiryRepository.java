package com.now.core.post.domain.repository;

import com.now.core.post.domain.Inquiry;
import com.now.core.post.domain.mapper.InquiryMapper;
import com.now.core.post.presentation.dto.Answer;
import com.now.core.post.presentation.dto.Condition;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 문의게시글 관련 정보를 관리하는 레포지토리
 */
@Repository
@RequiredArgsConstructor
public class InquiryRepository {
    
    private final InquiryMapper inquiryMapper;
    
    /**
     * 모든 문의 게시글 정보를 조회 후 반환
     *
     * @param condition 게시물 제한 정보를 담은 객체
     * @return 문의 게시글 정보 리스트
     */
    public List<Inquiry> findAllInquiries(Condition condition) {
        return inquiryMapper.findAllInquiries(condition);
    }

    /**
     * 문의 게시글 정보를 조회 후 반환
     *
     * @param postIdx 게시글 번호
     * @return 문의 게시글 정보
     */
    public Inquiry findInquiry(Long postIdx) {
        return inquiryMapper.findInquiry(postIdx);
    }

    /**
     * 문의 게시글 등록
     *
     * @param inquiry 등록할 문의 게시글 정보
     */
    public void saveInquiry(Inquiry inquiry) {
        inquiryMapper.saveInquiry(inquiry);
    }

    /**
     * 문의 게시글 비밀글 설정
     *
     * @param inquiry 등록할 문의 게시글 정보
     */
    public void saveInquirySecretSetting(Inquiry inquiry) {
        inquiryMapper.saveInquirySecretSetting(inquiry);
    }

    /**
     * 문의 게시글의 답변 등록
     *
     * @param answer 등록할 문의 게시글의 답변 정보
     */
    public void saveAnswer(Answer answer) {
        inquiryMapper.saveAnswer(answer);
    }


    /**
     * 문의 게시글 수정
     *
     * @param inquiry 수정할 문의 게시글 정보
     */
    public void updateInquiryPost(Inquiry inquiry) {
        inquiryMapper.updateInquiryPost(inquiry);
    }


    /**
     * 문의 게시글 수정
     *
     * @param inquiry 수정할 문의 게시글 정보
     */
    public void updateInquiry(Inquiry inquiry) {
        inquiryMapper.updateInquiry(inquiry);
    }

    /**
     * 문의 게시글의 답변 수정
     *
     * @param answer 등록할 문의 게시글의 답변 정보
     */
    public void updateAnswer(Answer answer) {
        inquiryMapper.updateAnswer(answer);
    }

    /**
     * 문의 게시글 삭제
     *
     * @param postIdx 게시글 번호
     */
    public void deleteInquiryOfPost(Long postIdx) {
        inquiryMapper.deleteInquiryOfPost(postIdx);
    }

    /**
     * 문의 게시글 삭제
     *
     * @param postIdx 게시글 번호
     */
    public void deleteInquiry(Long postIdx) {
        inquiryMapper.deleteInquiry(postIdx);
    }
}
