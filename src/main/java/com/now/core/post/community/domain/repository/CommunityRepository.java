package com.now.core.post.community.domain.repository;

import com.now.core.post.community.domain.Community;
import com.now.core.post.community.domain.mapper.CommunityMapper;
import com.now.core.post.common.presentation.dto.Condition;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 커뮤니티 게시글 관련 정보를 관리하는 레포지토리
 */
@Repository
@RequiredArgsConstructor
public class CommunityRepository {

    private final CommunityMapper communityMapper;

    /**
     * 모든 커뮤니티 게시글 정보를 조회 후 반환
     *
     * @param condition 게시물 제한 정보를 담은 객체
     * @return 커뮤니티 게시글 정보 리스트
     */
    public List<Community> findAllCommunity(Condition condition) {
        return communityMapper.findAllCommunity(condition);
    }


    /**
     * 커뮤니티 게시글 정보를 조회 후 반환
     *
     * @param postIdx 게시글 번호
     * @return 커뮤니티 게시글 정보
     */
    public Community findCommunity(Long postIdx) {
        return communityMapper.findCommunity(postIdx);
    }


    /**
     * 커뮤니티 게시글 등록
     *
     * @param community 등록할 커뮤니티 게시글 정보
     */
    public void saveCommunity(Community community) {
        communityMapper.saveCommunity(community);
    }


    /**
     * 커뮤니티 게시글 수정
     *
     * @param community 수정할 커뮤니티 게시글 정보
     */
    public void updateCommunity(Community community) {
        communityMapper.updateCommunity(community);
    }


    /**
     * 커뮤니티 게시글 삭제
     *
     * @param postIdx 게시글 번호
     */
    public void deleteCommunity(Long postIdx) {
        communityMapper.deleteCommunity(postIdx);
    }
}
