package com.now.core.post.application;

import com.now.common.exception.ErrorType;
import com.now.core.category.domain.constants.PostGroup;
import com.now.core.category.exception.InvalidCategoryException;
import com.now.core.comment.domain.CommentRepository;
import com.now.core.member.domain.Member;
import com.now.core.member.domain.MemberRepository;
import com.now.core.member.exception.InvalidMemberException;
import com.now.core.post.domain.Community;
import com.now.core.post.domain.PostRepository;
import com.now.core.post.exception.InvalidPostException;
import com.now.core.post.presentation.dto.Condition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 커뮤니티 게시글 관련 비즈니스 로직을 처리하는 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CommunityService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final CommentRepository commentRepository;

    /**
     * 모든 커뮤니티 게시글 정보를 조회 후 반환
     *
     * @return 커뮤니티 게시글 정보 리스트
     */
    public List<Community> getAllCommunities(Condition condition) {
        return postRepository.findAllCommunity(condition);
    }

    /**
     * 커뮤니티 게시글 등록
     *
     * @param community 등록할 커뮤니티 게시글 정보
     */
    public void registerCommunity(Community community) {
        Member member = getMember(community.getMemberId());

        if (!PostGroup.isCategoryInGroup(PostGroup.COMMUNITY, community.getCategory())) {
            throw new InvalidCategoryException(ErrorType.INVALID_CATEGORY);
        }

        postRepository.saveCommunity(community.updateMemberIdx(member.getMemberIdx()));
    }

    /**
     * 커뮤니티 게시글 수정
     *
     * @param community 수정할 커뮤니티 게시글 정보
     */
    public void updateCommunity(Community community) {
        Member member = getMember(community.getMemberId());

        if (!PostGroup.isCategoryInGroup(PostGroup.COMMUNITY, community.getCategory())) {
            throw new InvalidCategoryException(ErrorType.INVALID_CATEGORY);
        }

        postRepository.updateCommunity(community.updateMemberIdx(member.getMemberIdx()));
    }

    /**
     * 게시글 번호에 해당하는 게시글 삭제
     *
     * @param postIdx 게시글 번호
     */
    public void deleteCommunity(Long postIdx) {
        postRepository.deleteCommunity(postIdx);
    }

    /**
     * 게시글 수정 권한 확인
     *
     * @param postIdx  게시글 번호
     * @param memberId 회원 아이디
     */
    public void hasUpdateAccess(Long postIdx, String memberId) {
        Community community = getCommunity(postIdx);
        community.canUpdate(getMember(memberId));
    }

    /**
     * 게시글 삭제 권한 확인
     *
     * @param postIdx  게시글 번호
     * @param memberId 회원 아이디
     */
    public void hasDeleteAccess(Long postIdx, String memberId) {
        Community community = getCommunity(postIdx);
        community.canDelete(getMember(memberId), commentRepository.findAllByPostIdx(postIdx));
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
     * 커뮤니티 게시글 응답
     *
     * @param postIdx 게시글 번호
     * @return 공지 게시글 정보
     */
    public Community getCommunity(Long postIdx) {
        Community community = postRepository.findCommunity(postIdx);
        if (community == null) {
            throw new InvalidPostException(ErrorType.NOT_FOUND_POST);
        }

        return community;
    }

    /**
     * 커뮤니티 수정 게시글 응답
     * 
     * @param postIdx 게시글 번호
     * @param memberId 회원 ID
     * @return 커뮤니티 수정 게시글 정보
     */
    public Community getEditCommunity(Long postIdx, String memberId) {
        Community community = getCommunity(postIdx);
        Member member = getMember(memberId);
        
        community.canUpdate(member);
        return community;
    }
}

