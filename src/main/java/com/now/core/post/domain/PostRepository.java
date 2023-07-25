package com.now.core.post.domain;

import com.now.core.post.presentation.dto.Answer;
import com.now.core.post.presentation.dto.Condition;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 게시글 관련 정보를 관리하는 레포지토리
 */
@Repository
@RequiredArgsConstructor
public class PostRepository {

    public final PostMapper postMapper;

    /**
     * 모든 공지사항 게시글 정보를 조회 후 반환
     *
     * @param condition 게시물 제한 정보를 담은 객체
     * @return 공지사항 게시글 정보 리스트
     */
    public List<Notice> findAllNotices(Condition condition) {
        return postMapper.findAllNotices(condition);
    }

    /**
     * 모든 커뮤니티 게시글 정보를 조회 후 반환
     *
     * @param condition 게시물 제한 정보를 담은 객체
     * @return 커뮤니티 게시글 정보 리스트
     */
    public List<Community> findAllCommunity(Condition condition) {
        return postMapper.findAllCommunity(condition);
    }

    /**
     * 모든 사진 게시글 정보를 조회 후 반환
     *
     * @param condition 게시물 제한 정보를 담은 객체
     * @return 사진 게시글 정보 리스트
     */
    public List<Photo> findAllPhotos(Condition condition) {
        return postMapper.findAllPhotos(condition);
    }

    /**
     * 모든 문의 게시글 정보를 조회 후 반환
     *
     * @param condition 게시물 제한 정보를 담은 객체
     * @return 문의 게시글 정보 리스트
     */
    public List<Inquiry> findAllInquiries(Condition condition) {
        return postMapper.findAllInquiries(condition);
    }


    /**
     * 공지 게시글 정보를 조회 후 반환
     *
     * @param postIdx 게시글 번호
     * @return 공지 게시글 정보
     */
    public Notice findNotice(Long postIdx) {
        return postMapper.findNotice(postIdx);
    }

    /**
     * 커뮤니티 게시글 정보를 조회 후 반환
     *
     * @param postIdx 게시글 번호
     * @return 커뮤니티 게시글 정보
     */
    public Community findCommunity(Long postIdx) {
        return postMapper.findCommunity(postIdx);
    }

    /**
     * 사진 게시글 정보를 조회 후 반환
     *
     * @param postIdx 게시글 번호
     * @return 사진 게시글 정보
     */
    public Photo findPhoto(Long postIdx) {
        return postMapper.findPhoto(postIdx);
    }

    /**
     * 문의 게시글 정보를 조회 후 반환
     *
     * @param postIdx 게시글 번호
     * @return 문의 게시글 정보
     */
    public Inquiry findInquiry(Long postIdx) {
        return postMapper.findInquiry(postIdx);
    }

    /**
     * 게시글 번호에 해당하는 게시글이 있다면 true 반환, 그렇지 않다면 false 반환
     *
     * @param postIdx 게시글 번호
     * @return 게시글이 있다면 true 반환, 그렇지 않다면 false 반환
     */
    public boolean existPostByPostId(Long postIdx) {
        return postMapper.existPostByPostId(postIdx);
    }





    
    /**
     * 공지 게시글 등록
     * 
     * @param notice  등록할 공지 게시글 정보
     */
    public void saveNotice(Notice notice) {
        postMapper.saveNotice(notice);
    }

    /**
     * 커뮤니티 게시글 등록
     *
     * @param community 등록할 커뮤니티 게시글 정보
     */
    public void saveCommunity(Community community) {
        postMapper.saveCommunity(community);
    }

    /**
     * 사진 게시글 등록
     *
     * @param photo  등록할 사진 게시글 정보
     */
    public void savePhoto(Photo photo) {
        postMapper.savePhoto(photo);
    }

    /**
     * 문의 게시글 등록
     *
     * @param inquiry  등록할 문의 게시글 정보
     */
    public void saveInquiry(Inquiry inquiry) {
        postMapper.saveInquiry(inquiry);
        postMapper.saveInquirySecretSetting(inquiry);
    }

    /**
     * 문의 게시글의 답변 등록
     *
     * @param answer  등록할 문의 게시글의 답변 정보
     */
    public void saveAnswer(Answer answer) {
        postMapper.saveAnswer(answer);
    }






    /**
     * 공지 게시글 수정
     *
     * @param notice 수정할 공지 게시글 정보
     */
    public void updateNotice(Notice notice) {
        postMapper.updateNotice(notice);
    }

    /**
     * 커뮤니티 게시글 수정
     *
     * @param community 수정할 커뮤니티 게시글 정보
     */
    public void updateCommunity(Community community) {
        postMapper.updateCommunity(community);
    }

    /**
     * 사진 게시글 수정
     *
     * @param photo 수정할 사진 게시물 정보
     */
    public void updatePhoto(Photo photo) {
        postMapper.updatePhoto(photo);
    }

    /**
     * 문의 게시글 수정
     *
     * @param inquiry 수정할 문의 게시글 정보
     */
    public void updateInquiry(Inquiry inquiry) {
        postMapper.updateInquiryPost(inquiry);
        postMapper.updateInquiry(inquiry);
    }

    /**
     * 문의 게시글의 답변 수정
     *
     * @param answer  등록할 문의 게시글의 답변 정보
     */
    public void updateAnswer(Answer answer) {
        postMapper.updateAnswer(answer);
    }






    /**
     * 공지 게시글 삭제
     *
     * @param postIdx 삭제할 공지 게시글 번호
     */
    public void deleteNotice(Long postIdx) {
        postMapper.deletePost(postIdx);
    }

    /**
     * 커뮤니티 게시글 삭제
     *
     * @param postIdx 게시글 번호
     */
    public void deleteCommunity(Long postIdx) {
        postMapper.deletePost(postIdx);
    }

    /**
     * 사진 게시글 삭제
     *
     * @param postIdx 삭제할 게시글 번호
     */
    public void deletePhoto(Long postIdx) {
        postMapper.deletePost(postIdx);
    }

    /**
     * 문의 게시글 삭제
     *
     * @param postIdx 게시글 번호
     */
    public void deleteInquiry(Long postIdx) {
        postMapper.deletePost(postIdx);
        postMapper.deleteInquiry(postIdx);
    }
}
