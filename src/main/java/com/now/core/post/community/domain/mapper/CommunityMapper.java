package com.now.core.post.community.domain.mapper;

import com.now.core.post.community.domain.Community;
import com.now.core.post.common.presentation.dto.Condition;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 커뮤니티 게시글 정보에 접근하는 매퍼 인터페이스
 */
@Mapper
public interface CommunityMapper {


    /**
     * 모든 커뮤니티 게시글 정보를 조회 후 반환
     *
     * @param condition 게시물 제한 정보를 담은 객체
     * @return 커뮤니티 게시글 정보 리스트
     */
    List<Community> findAllCommunity(Condition condition);


    /**
     * 커뮤니티 게시글 정보를 조회 후 반환
     *
     * @param postIdx 게시글 번호
     * @return 커뮤니티 게시글 정보
     */
    Community findCommunity(Long postIdx);


    /**
     * 커뮤니티 게시글 등록
     *
     * @param community 등록할 커뮤니티 게시글 정보
     */
    void saveCommunity(Community community);


    /**
     * 커뮤니티 게시글 수정
     *
     * @param community 수정할 커뮤니티 게시글 정보
     */
    void updateCommunity(Community community);


    /**
     * 커뮤니티 게시글 삭제
     *
     * @param postIdx 게시글 번호
     */
    void deleteCommunity(Long postIdx);
}
