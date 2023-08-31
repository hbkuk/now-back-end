package com.now.core.post.inquiry.application;

import com.now.common.exception.ErrorType;
import com.now.common.security.PasswordSecurityManager;
import com.now.core.category.domain.constants.PostGroup;
import com.now.core.comment.domain.CommentRepository;
import com.now.core.member.domain.Member;
import com.now.core.member.domain.MemberRepository;
import com.now.core.member.exception.InvalidMemberException;
import com.now.core.post.common.exception.CannotCreatePostException;
import com.now.core.post.common.exception.CannotUpdatePostException;
import com.now.core.post.common.exception.InvalidPostException;
import com.now.core.post.common.presentation.dto.Condition;
import com.now.core.post.inquiry.domain.Inquiry;
import com.now.core.post.inquiry.domain.constants.PrivacyUpdateOption;
import com.now.core.post.inquiry.domain.repository.InquiryRepository;
import com.now.core.post.inquiry.exception.CannotViewInquiryException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 문의 게시글 관련 비즈니스 로직을 처리하는 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InquiryService {

    private final InquiryRepository inquiryRepository;
    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final PasswordSecurityManager passwordSecurityManager;

    /**
     * 모든 문의 게시글 정보를 조회 후 반환
     *
     * @return 문의 게시글 정보 리스트
     */
    public List<Inquiry> getAllInquiries(Condition condition) {
        return inquiryRepository.findAllInquiries(condition);
    }

    /**
     * 문의 게시글 응답
     *
     * @param postIdx 게시글 번호
     * @return 문의 게시글 정보
     */
    public Inquiry getPublicInquiry(Long postIdx) {
        Inquiry inquiry = getInquiry(postIdx);

        if (inquiry.getSecret()) {
            throw new CannotViewInquiryException(ErrorType.CAN_NOT_VIEW_SECRET_INQUIRY);
        }
        return inquiry;
    }

    /**
     * 문의 게시글 응답
     *
     * @param postIdx  게식글 번호
     * @param memberId 회원 아이디
     * @param password 비밀번호
     * @return 문의 게시글 정보
     */
    public Inquiry getPrivateInquiry(Long postIdx, String memberId, String password) {
        Inquiry inquiry = getInquiry(postIdx);

        if (inquiry.getSecret()) {
            if (memberId != null) {
                inquiry.canView(getMember(memberId));
                return inquiry;
            }
            if (!isPasswordMatching(password, inquiry.getPassword())) {
                throw new CannotViewInquiryException(ErrorType.CAN_NOT_VIEW_INQUIRY_PASSWORD_NOT_MATCH);
            }
        }
        return inquiry;
    }

    /**
     * 문의 게시글 등록
     *
     * @param inquiry 등록할 문의 게시글 정보
     */
    public void registerInquiry(Inquiry inquiry) {
        Member member = getMember(inquiry.getMemberId());

        if (!PostGroup.isCategoryInGroup(PostGroup.INQUIRY, inquiry.getCategory())) {
            throw new CannotCreatePostException(ErrorType.NOT_FOUND_CATEGORY);
        }

        inquiryRepository.savePost(inquiry.updateMemberIdx(member.getMemberIdx())
                .updatePassword(passwordSecurityManager.encodeWithSalt(inquiry.getPassword())));
        inquiryRepository.saveInquirySecretSetting(inquiry);
    }

    /**
     * 문의 게시글 수정
     *
     * @param updatedInquiry      수정할 문의 게시글 정보
     * @param privacyUpdateOption 개인 정보 옵션
     */
    public void updateAndHandleInquiry(Inquiry updatedInquiry, PrivacyUpdateOption privacyUpdateOption) {
        Member member = getMember(updatedInquiry.getMemberId());
        processUpdatedInquiry(updatedInquiry.updateMemberIdx(member.getMemberIdx()), privacyUpdateOption);
    }

    /**
     * 수정된 문의 게시글 처리
     *
     * @param updatedInquiry      수정된 문의 게시글 정보
     * @param privacyUpdateOption 개인 정보 옵션
     */
    private void processUpdatedInquiry(Inquiry updatedInquiry, PrivacyUpdateOption privacyUpdateOption) {
        if (!updatedInquiry.getSecret()) {
            processPublicInquiryUpdate(updatedInquiry, privacyUpdateOption);
        }
        if (updatedInquiry.getSecret()) {
            processPrivateInquiryUpdate(updatedInquiry
                    .updatePassword(passwordSecurityManager.encodeWithSalt(updatedInquiry.getPassword())), privacyUpdateOption);
        }
    }

    /**
     * 문의 게시글 삭제
     *
     * @param postIdx  삭제할 게시글 번호
     * @param memberId 회원 아이디
     */
    public void deleteInquiry(Long postIdx, String memberId) {
        Member member = getMember(memberId);

        inquiryRepository.deleteInquiry(postIdx);
        inquiryRepository.deletePost(postIdx);
    }

    /**
     * 게시글 수정 권한 확인
     *
     * @param updatedInquiry 수정할 게시글 정보
     */
    public void verifyInquiryUpdatePermission(Inquiry updatedInquiry, PrivacyUpdateOption privacyUpdateOption) {
        Inquiry inquiry = getInquiry(updatedInquiry.getPostIdx());
        inquiry.canUpdate(getMember(updatedInquiry.getMemberId()));

        if (!PostGroup.isCategoryInGroup(PostGroup.INQUIRY, updatedInquiry.getCategory())) {
            throw new CannotUpdatePostException(ErrorType.NOT_FOUND_CATEGORY);
        }

        Inquiry existInquiry = getInquiry(updatedInquiry.getPostIdx());
        if (!privacyUpdateOption.canUpdate(existInquiry.getSecret())) {
            throw new CannotUpdatePostException(ErrorType.INVALID_SECRET);
        }

        if (!updatedInquiry.canUpdateWithPrivacyOption(privacyUpdateOption)) {
            throw new CannotCreatePostException(ErrorType.INVALID_SECRET);
        }
    }

    /**
     * 게시글 삭제 권한 확인
     *
     * @param postIdx  게시글 번호
     * @param memberId 회원 아이디
     */
    public void hasDeleteAccess(Long postIdx, String memberId) {
        Inquiry inquiry = getInquiry(postIdx);
        inquiry.canDelete(getMember(memberId), commentRepository.findAllByPostIdx(postIdx));
    }

    /**
     * 문의 게시글 조회
     *
     * @param postIdx 게시글 번호
     * @return 문의 게시글
     */
    public Inquiry getInquiry(Long postIdx) {
        Inquiry inquiry = inquiryRepository.findInquiry(postIdx);
        if (inquiry == null) {
            throw new InvalidPostException(ErrorType.NOT_FOUND_POST);
        }
        return inquiry;
    }

    /**
     * 수정 문의 게시글 조회
     *
     * @param postIdx  게시글 번호
     * @param memberId 회원 아이디
     * @return 수정 문의 게시글
     */
    public Inquiry getEditInquiry(Long postIdx, String memberId) {
        Inquiry inquiry = getInquiry(postIdx);
        Member member = getMember(memberId);

        inquiry.canUpdate(member);
        return inquiry;
    }

    
    /**
     * 비밀글로 변경 혹은 비밀번호를 변경할 때 처리
     *
     * @param updatedInquiry       수정된 문의 게시글 정보
     * @param privacyUpdateOption 개인 정보 옵션
     */
    private void processPrivateInquiryUpdate(Inquiry updatedInquiry, PrivacyUpdateOption privacyUpdateOption) {
        if (PrivacyUpdateOption.TO_PRIVATE == privacyUpdateOption) {
            processPrivateOrPasswordChangeInquiryUpdate(updatedInquiry);
        }
        if (PrivacyUpdateOption.CHANGE_PASSWORD == privacyUpdateOption) {
            processPrivateOrPasswordChangeInquiryUpdate(updatedInquiry);
        }
        if (PrivacyUpdateOption.KEEP_PASSWORD == privacyUpdateOption) {
            processKeepPasswordInquiryUpdate(updatedInquiry);
        }
    }

    
    /**
     * 비밀번호 유지 옵션일 때 처리
     *
     * @param updatedInquiry 수정된 문의 게시글 정보
     */
    private void processKeepPasswordInquiryUpdate(Inquiry updatedInquiry) {
        inquiryRepository.updatePost(updatedInquiry.updateMemberIdx(updatedInquiry.getMemberIdx()));
    }

    
    /**
     * 비밀글로 변경하거나 비밀번호 변경 시 처리
     *
     * @param updatedInquiry 수정된 문의 게시글 정보
     */
    private void processPrivateOrPasswordChangeInquiryUpdate(Inquiry updatedInquiry) {
        processKeepPasswordInquiryUpdate(updatedInquiry);
        inquiryRepository.updateInquiry(updatedInquiry);
    }


    /**
     * 공개 문의 게시글 업데이트
     *
     * @param updateInquiry       수정할 문의 게시글 정보
     * @param privacyUpdateOption 개인 정보 업데이트 옵션
     */
    private void processPublicInquiryUpdate(Inquiry updateInquiry, PrivacyUpdateOption privacyUpdateOption) {
        if (PrivacyUpdateOption.TO_PUBLIC == privacyUpdateOption) {
            processKeepPasswordInquiryUpdate(updateInquiry);
            inquiryRepository.updateInquiryNonSecretSetting(updateInquiry.getPostIdx());
        }
    }


    /**
     * 회원 정보 응답
     *
     * @param memberId 회원 아이디
     * @return 회원 도메인 객체
     */
    private Member getMember(String memberId) {
        Member member = memberRepository.findById(memberId);
        if (member == null) {
            throw new InvalidMemberException(ErrorType.NOT_FOUND_MEMBER);
        }
        return member;
    }

    /**
     * 주어진 패스워드가 기존 패스워드와 일치하는지 확인합니다.
     *
     * @param existingPassword 기존 패스워드
     * @param providedPassword 주어진 패스워드
     * @return 주어진 패스워드가 기존 패스워드와 일치하면 {@code true}를 반환하고, 그렇지 않으면 {@code false}를 반환합니다.
     */
    private boolean isPasswordMatching(String existingPassword, String providedPassword) {
        return existingPassword != null && passwordSecurityManager.matchesWithSalt(existingPassword, providedPassword);
    }
}

