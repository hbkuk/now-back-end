package com.now.core.post.domain;

import com.now.core.post.domain.abstractions.ManagerPost;
import com.now.core.post.domain.abstractions.MemberPost;
import com.now.core.post.presentation.dto.Condition;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 게시글 정보에 접근하는 매퍼 인터페이스
 */
@Mapper
public interface PostMapper {

    /**
     * 모든 공지사항 게시글 정보를 조회 후 반환
     *
     * @return 공지사항 게시글 정보 리스트
     */
    List<Notice> findAllNotices(Condition condition);

    /**
     * 모든 커뮤니티 게시글 정보를 조회 후 반환
     *
     * @return 커뮤니티 게시글 정보 리스트
     */
    List<Community> findAllCommunity(Condition condition);

    /**
     * 모든 사진 게시글 정보를 조회 후 반환
     *
     * @return 사진 게시글 정보 리스트
     */
    List<Photo> findAllPhotos(Condition condition);

    /**
     * 모든 문의 게시글 정보를 조회 후 반환
     *
     * @return 문의 게시글 정보 리스트
     */
    List<Inquiry> findAllInquiries(Condition condition);

    /**
     * 매니저가 작성한 게시글을 저장
     *
     * @param managerPost 저장할 게시글 정보
     */
    void saveManagerPost(ManagerPost managerPost);

    /**
     * 회원이 작성한 게시글을 저장
     *
     * @param memberPost 저장할 게시글 정보
     */
    void saveMemberPost(MemberPost memberPost);
}
