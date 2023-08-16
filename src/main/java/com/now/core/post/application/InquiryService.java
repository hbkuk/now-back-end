package com.now.core.post.application;

import com.now.common.exception.ErrorType;
import com.now.common.security.PasswordSecurityManager;
import com.now.core.category.domain.constants.PostGroup;
import com.now.core.comment.domain.CommentRepository;
import com.now.core.member.domain.Member;
import com.now.core.member.domain.MemberRepository;
import com.now.core.member.exception.InvalidMemberException;
import com.now.core.post.domain.Inquiry;
import com.now.core.post.domain.repository.InquiryRepository;
import com.now.core.post.exception.CannotCreatePostException;
import com.now.core.post.exception.CannotViewInquiryException;
import com.now.core.post.exception.InvalidPostException;
import com.now.core.post.presentation.dto.Condition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
    @Cacheable(value = "inquiryCache", key="#condition.hashCode()")
    public List<Inquiry> getAllInquiries(Condition condition) {
        return inquiryRepository.findAllInquiries(condition);
    }

    /**
     * 문의 게시글 응답
     *
     * @param postIdx 게시글 번호
     * @return 문의 게시글 정보
     */
    @Cacheable(value = "postCache", key="#postIdx")
    public Inquiry getInquiryWithSecretCheck(Long postIdx) {
        Inquiry inquiry = getInquiry(postIdx);

        if (inquiry.getSecret()) {
            throw new CannotViewInquiryException(ErrorType.CAN_NOT_VIEW_SECRET_INQUIRY);
        }
        return inquiry;
    }

    /**
     * 문의 게시글 응답
     *
     * @param postIdx 게시글 번호
     * @param memberId 회원 아이디
     * @return 문의 게시글 정보
     */
    public Inquiry getInquiryWithSecretCheck(Long postIdx, String memberId, String password) {
        Inquiry inquiry = getInquiry(postIdx);

        if (inquiry.getSecret()) {
            if (isPasswordMatching(password, inquiry.getPassword())) {
                return inquiry;
            }
            inquiry.canView(getMember(memberId));
        }
        return inquiry;
    }

    /**
     * 문의 게시글 등록
     *
     * @param inquiry 등록할 문의 게시글 정보
     */
    @CacheEvict(value = {"postCache", "inquiryCache"}, allEntries = true)
    public void registerInquiry(Inquiry inquiry) {
        Member member = getMember(inquiry.getMemberId());

        if (!PostGroup.isCategoryInGroup(PostGroup.INQUIRY, inquiry.getCategory())) {
            throw new CannotCreatePostException(ErrorType.NOT_FOUND_CATEGORY);
        }

        inquiryRepository.saveInquiry(inquiry.updateMemberIdx(member.getMemberIdx())
                                            .updatePassword(passwordSecurityManager.encodeWithSalt(inquiry.getPassword())));
    }

    /**
     * 문의 게시글 수정
     *
     * @param updateInquiry 수정할 문의 게시글 정보
     */
    @CacheEvict(value = {"postCache", "inquiryCache"}, allEntries = true)
    public void updateInquiry(Inquiry updateInquiry) {
        Member member = getMember(updateInquiry.getMemberId());

        if (!PostGroup.isCategoryInGroup(PostGroup.INQUIRY, updateInquiry.getCategory())) {
            throw new CannotCreatePostException(ErrorType.NOT_FOUND_CATEGORY);
        }
        inquiryRepository.updateInquiry(updateInquiry.updateMemberIdx(member.getMemberIdx()));
    }

    /**
     * 문의 게시글 삭제
     *
     * @param postIdx  삭제할 게시글 번호
     * @param memberId 회원 아이디
     */
    @CacheEvict(value = {"postCache", "inquiryCache"}, allEntries = true)
    public void deleteInquiry(Long postIdx, String memberId) {
        Member member = getMember(memberId);

        inquiryRepository.deleteInquiry(postIdx);
    }

    /**
     * 게시글 수정 권한 확인
     *
     * @param postIdx 게시글 번호
     * @param memberId 회원 아이디
     */
    public void hasUpdateAccess(Long postIdx, String memberId) {
        Inquiry inquiry = getInquiry(postIdx);
        inquiry.canUpdate(getMember(memberId));
    }

    /**
     * 게시글 삭제 권한 확인
     *
     * @param postIdx 게시글 번호
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
     * @param postIdx 게시글 번호
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
     * 회원 정보 응답
     *
     * @param memberId 회원 아이디
     * @return 회원 도메인 객체
     */
    private Member getMember(String memberId) {
        Member member = memberRepository.findById(memberId);
        if(member == null) {
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

