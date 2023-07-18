package com.now.core.post.application;

import com.now.common.exception.ErrorType;
import com.now.common.exception.ForbiddenException;
import com.now.core.authentication.application.JwtTokenService;
import com.now.core.category.domain.constants.PostGroup;
import com.now.core.comment.application.CommentService;
import com.now.core.member.application.MemberService;
import com.now.core.member.domain.Member;
import com.now.core.post.domain.Inquiry;
import com.now.core.post.domain.PostRepository;
import com.now.core.post.exception.CannotCreatePostException;
import com.now.core.post.exception.InvalidPostException;
import com.now.core.post.presentation.dto.Condition;
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

    private final PostRepository postRepository;
    private final CommentService commentService;
    private final MemberService memberService;
    private final JwtTokenService jwtTokenService;

    /**
     * 모든 문의 게시글 정보를 조회 후 반환
     *
     * @return 문의 게시글 정보 리스트
     */
    public List<Inquiry> retrieveAllInquiries(Condition condition) {
        return postRepository.findAllInquiries(condition);
    }

    /**
     * 문의 게시글 응답
     *
     * @param postIdx 게시글 번호
     * @return 문의 게시글 정보
     */
    public Inquiry findInquiry(Long postIdx, String token) {
        Inquiry inquiry = postRepository.findInquiry(postIdx);
        if (inquiry == null) {
            throw new InvalidPostException(ErrorType.NOT_FOUND_POST);
        }

        if (inquiry.getSecret()) {
            inquiry.canView(memberService.findMemberById((String) jwtTokenService.getClaim(token, "id")));
        }
        return inquiry;
    }

    /**
     * 문의 게시글 등록
     *
     * @param inquiry 등록할 문의 게시글 정보
     */
    public void registerInquiry(Inquiry inquiry) {
        Member member = memberService.findMemberById(inquiry.getMemberId());

        if (!PostGroup.isCategoryInGroup(PostGroup.INQUIRY, inquiry.getCategory())) {
            throw new CannotCreatePostException(ErrorType.INVALID_CATEGORY);
        }

        postRepository.saveInquiry(inquiry.updateMemberIdx(member.getMemberIdx()));
    }

    /**
     * 문의 게시글 수정
     *
     * @param updateInquiry 수정할 문의 게시글 정보
     */
    public void updateInquiry(Inquiry updateInquiry) {
        Member member = memberService.findMemberById(updateInquiry.getMemberId());

        if (!PostGroup.isCategoryInGroup(PostGroup.INQUIRY, updateInquiry.getCategory())) {
            throw new CannotCreatePostException(ErrorType.INVALID_CATEGORY);
        }
        postRepository.updateInquiry(updateInquiry.updateMemberIdx(member.getMemberIdx()));
    }

    /**
     * 문의 게시글 삭제
     *
     * @param postIdx  삭제할 게시글 번호
     * @param memberId 회원 아이디
     */
    public void deleteInquiry(Long postIdx, String memberId) {
        Member member = memberService.findMemberById(memberId);
        Inquiry inquiry = postRepository.findInquiry(postIdx);

        if (!inquiry.canDelete(member, commentService.findAllByPostIdx(postIdx))) {
            throw new ForbiddenException(ErrorType.FORBIDDEN);
        }
        postRepository.deleteInquiry(postIdx);
    }

    /**
     * 게시글 수정 권한 확인
     *
     * @param postIdx 게시글 번호
     * @param memberId 회원 아이디
     */
    public void hasUpdateAccess(Long postIdx, String memberId) {
        Inquiry inquiry = postRepository.findInquiry(postIdx);
        inquiry.canUpdate(memberService.findMemberById(memberId));
    }

    /**
     * 게시글 삭제 권한 확인
     *
     * @param postIdx 게시글 번호
     * @param memberId 회원 아이디
     */
    public void hasDeleteAccess(Long postIdx, String memberId) {
        Inquiry inquiry = postRepository.findInquiry(postIdx);
        inquiry.canDelete(memberService.findMemberById(memberId), commentService.findAllByPostIdx(postIdx));
    }
}

