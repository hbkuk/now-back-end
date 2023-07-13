package com.now.core.post.domain;

import com.now.core.post.domain.abstractions.ManagerPost;
import com.now.core.post.domain.abstractions.MemberPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 게시글 관련 정보를 관리하는 리포지토리
 */
@Repository
public class PostRepository {

    public PostMapper postMapper;

    @Autowired
    public PostRepository(PostMapper postMapper) {
        this.postMapper = postMapper;
    }


    /**
     * 모든 공지사항 게시글 정보를 조회 후 반환
     *
     * @return 공지사항 게시글 정보 리스트
     */
    public List<Notice> findAllNotices() {
        return postMapper.findAllNotices();
    }

    /**
     * 모든 커뮤니티 게시글 정보를 조회 후 반환
     *
     * @return 커뮤니티 게시글 정보 리스트
     */
    public List<Community> findAllCommunity() {
        return postMapper.findAllCommunity();
    }

    /**
     * 모든 사진 게시글 정보를 조회 후 반환
     *
     * @return 사진 게시글 정보 리스트
     */
    public List<Photo> findAllPhotos() {
        return postMapper.findAllPhotos();
    }

    /**
     * 모든 문의 게시글 정보를 조회 후 반환
     *
     * @return 문의 게시글 정보 리스트
     */
    public List<Inquiry> findAllInquiries() {
        return postMapper.findAllInquiries();
    }

    /**
     * 매니저가 작성한 게시글을 저장
     *
     * @param managerPost 저장할 게시글 정보
     */
    public void saveManagerPost(ManagerPost managerPost) {
        if (managerPost instanceof Notice) {
            Notice notice = (Notice) managerPost;
            postMapper.saveManagerPost(notice);
        }
    }

    /**
     * 회원이 작성한 게시글을 저장
     *
     * @param memberPost 저장할 게시글 정보
     */
    // TODO: Refactoring
    public void saveMemberPost(MemberPost memberPost) {
        if (memberPost instanceof Community) {
            Community community = (Community) memberPost;
            postMapper.saveMemberPost(community);
        }

        if (memberPost instanceof Photo) {
            Photo photo = (Photo) memberPost;
            postMapper.saveMemberPost(photo);
        }

        if (memberPost instanceof Inquiry) {
            Inquiry inquiry = (Inquiry) memberPost;
            postMapper.saveMemberPost(inquiry);
        }
    }
}
