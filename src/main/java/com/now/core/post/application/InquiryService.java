package com.now.core.post.application;

import com.now.core.authentication.application.JwtTokenService;
import com.now.core.authentication.constants.Authority;
import com.now.core.category.domain.constants.PostGroup;
import com.now.core.manager.application.ManagerService;
import com.now.core.manager.domain.Manager;
import com.now.core.member.application.MemberService;
import com.now.core.member.domain.Member;
import com.now.core.post.domain.Inquiry;
import com.now.core.post.domain.PostRepository;
import com.now.core.post.exception.CannotWritePostException;
import com.now.core.post.exception.PermissionDeniedException;
import com.now.core.post.presentation.dto.Answer;
import com.now.core.post.presentation.dto.Condition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * 문의 게시글 관련 비즈니스 로직을 처리하는 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InquiryService {

    private final PostRepository postRepository;
    private final MemberService memberService;
    private final ManagerService managerService;
    private final JwtTokenService jwtTokenService;
    private final MessageSourceAccessor messageSource;

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
        if(inquiry == null) {
            throw new NoSuchElementException(messageSource.getMessage("error.noSuch.post"));
        }

        if(inquiry.getSecret()) {
            if (token == null) {
                throw new PermissionDeniedException(messageSource.getMessage("error.permission.denied"));
            }

            Authority authority = Authority.valueOf(jwtTokenService.getClaim(token, "role").toString());
            if (!Authority.hasAccess(authority)) {
                inquiry.canView(memberService.findMemberById(jwtTokenService.getClaim(token, "id").toString()));
            }
        }
        return inquiry;
    }

    /**
     * 문의 게시글 등록
     *
     * @param inquiry   등록할 문의 게시글 정보
     * @param authority 권한
     */
    public void registerInquiry(Inquiry inquiry, Authority authority) {
        if (authority != Authority.MEMBER) {
            throw new PermissionDeniedException(messageSource.getMessage("error.permission.denied"));
        }

        if (!PostGroup.isCategoryInGroup(inquiry.getCategory(), inquiry.getPostGroup())) {
            throw new CannotWritePostException(messageSource.getMessage("error.write.failed"));
        }

        Member member = memberService.findMemberById(inquiry.getMemberId());

        postRepository.saveInquiry(inquiry.updateMemberIdx(member.getMemberIdx()));
    }

    /**
     * 문의 게시글의 답변 등록
     *
     * @param answer    등록할 답변 정보
     * @param authority 권한
     */
    public void registerAnswer(Answer answer, Authority authority) {
        if (authority != Authority.MANAGER) {
            throw new PermissionDeniedException(messageSource.getMessage("error.permission.denied"));
        }

        Manager manager = managerService.findManagerById(answer.getAnswerManagerId());

        postRepository.saveAnswer(answer.updateaAswerManagerIdx(manager.getManagerIdx()));
    }
}

