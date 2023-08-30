package com.now.core.post.inquiry.domain.mapper;

import com.now.core.post.inquiry.domain.Inquiry;
import com.now.core.post.inquiry.presentation.dto.Answer;
import com.now.core.post.common.presentation.dto.Condition;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 문의 게시글 정보에 접근하는 매퍼 인터페이스
 */
@Mapper
public interface InquiryMapper {

    /**
     * 모든 문의 게시글 정보를 조회 후 반환
     *
     * @param condition 게시물 제한 정보를 담은 객체
     * @return 문의 게시글 정보 리스트
     */
    List<Inquiry> findAllInquiries(Condition condition);


    /**
     * 문의 게시글 정보를 조회 후 반환
     *
     * @param postIdx 게시글 번호
     * @return 문의 게시글 정보
     */
    Inquiry findInquiry(Long postIdx);

    /**
     * 문의 게시글 등록
     *
     * @param inquiry 등록할 문의 게시글 정보
     */
    void savePost(Inquiry inquiry);

    /**
     * 문의 게시글 비밀글 설정
     *
     * @param inquiry 등록할 문의 게시글 정보
     */
    void saveInquirySecretSetting(Inquiry inquiry);

    /**
     * 문의 게시글의 답변 등록
     *
     * @param answer 등록할 문의 게시글의 답변 정보
     */
    void saveAnswer(Answer answer);

    /**
     * 문의 게시글 수정
     *
     * @param inquiry 수정할 문의 게시글 정보
     */
    void updatePost(Inquiry inquiry);

    /**
     * 문의 게시글 수정
     *
     * @param inquiry 수정할 문의 게시글 정보
     */
    void updateInquiry(Inquiry inquiry);

    /**
     * 문의 게시글 공개글 수정
     *
     * @param postIdx 수정할 문의 게시글 번호
     */
    void updateInquiryNonSecretSetting(Long postIdx);


    /**
     * 문의 게시글의 답변 수정
     *
     * @param answer 수정할 문의 게시글의 답변 정보
     */
    void updateAnswer(Answer answer);

    /**
     * 게시글 삭제
     *
     * @param postIdx 게시글 번호
     */
    void deletePost(Long postIdx);

    /**
     * 문의 테이블 삭제
     *
     * @param postIdx 게시글 번호
     */
    void deleteInquiry(Long postIdx);
}
