package com.now.core.post.domain;

import com.now.core.post.presentation.dto.Answer;
import com.now.core.post.presentation.dto.Condition;
import com.now.core.post.presentation.dto.Posts;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 게시글 정보에 접근하는 매퍼 인터페이스
 */
@Mapper
public interface PostMapper {


    /**
     * 모든 게시글 정보를 조회 후 반환
     *
     * @param condition 게시물 제한 정보를 담은 객체
     * @return 게시글 정보 리스트
     */
    List<Posts> findAllPosts(Condition condition);


    /**
     * 조건에 맞는 게시물을 조회 후 수량 반환
     *
     * @param condition 조건 객체
     * @return 조건에 맞는 게시물을 조회 후 수량 반환
     */
    Long findTotalPostCount(Condition condition);


    /**
     * 상단에 고정된 공지 게시물과 조건에 맞는 게시물 정보를 조회 후 반환
     *
     * @param condition 게시물 제한 정보를 담은 객체
     * @return 공지사항 게시글 정보 리스트
     */
    List<Notice> findAllNoticesWithPin(Condition condition);


    /**
     * 모든 공지사항 게시글 정보를 조회 후 반환
     *
     * @param condition 게시물 제한 정보를 담은 객체
     * @return 공지사항 게시글 정보 리스트
     */
    List<Notice> findAllNotices(Condition condition);

    /**
     * 모든 커뮤니티 게시글 정보를 조회 후 반환
     *
     * @param condition 게시물 제한 정보를 담은 객체
     * @return 커뮤니티 게시글 정보 리스트
     */
    List<Community> findAllCommunity(Condition condition);

    /**
     * 모든 사진 게시글 정보를 조회 후 반환
     *
     * @param condition 게시물 제한 정보를 담은 객체
     * @return 사진 게시글 정보 리스트
     */
    List<Photo> findAllPhotos(Condition condition);

    /**
     * 모든 문의 게시글 정보를 조회 후 반환
     *
     * @param condition 게시물 제한 정보를 담은 객체
     * @return 문의 게시글 정보 리스트
     */
    List<Inquiry> findAllInquiries(Condition condition);

    /**
     * 공지 게시글 정보를 조회 후 반환
     *
     * @param postIdx 게시글 번호
     * @return 공지 게시글 정보
     */
    Notice findNotice(Long postIdx);

    /**
     * 커뮤니티 게시글 정보를 조회 후 반환
     *
     * @param postIdx 게시글 번호
     * @return 커뮤니티 게시글 정보
     */
    Community findCommunity(Long postIdx);

    /**
     * 사진 게시글 정보를 조회 후 반환
     *
     * @param postIdx 게시글 번호
     * @return 사진 게시글 정보
     */
    Photo findPhoto(Long postIdx);

    /**
     * 문의 게시글 정보를 조회 후 반환
     *
     * @param postIdx 게시글 번호
     * @return 문의 게시글 정보
     */
    Inquiry findInquiry(Long postIdx);


    /**
     * 주어진 게시물 번호에 해당하는 게시물이 존재한다면 true, 그렇지 않다면 false 반환
     *
     * @param postIdx 게시글 번호
     * @return 주어진 게시물 번호에 해당하는 게시물이 존재한다면 true, 그렇지 않다면 false 반환
     */
    boolean existPostByPostId(Long postIdx);


    /**
     * 공지 게시글 등록
     *
     * @param notice 등록할 공지 게시글 정보
     */
    void saveNotice(Notice notice);

    /**
     * 커뮤니티 게시글 등록
     *
     * @param community 등록할 커뮤니티 게시글 정보
     */
    void saveCommunity(Community community);

    /**
     * 사진 게시글 등록
     *
     * @param photo 등록할 사진 게시글 정보
     */
    void savePhoto(Photo photo);

    /**
     * 문의 게시글 등록
     *
     * @param inquiry 등록할 문의 게시글 정보
     */
    void saveInquiry(Inquiry inquiry);

    /**
     * 문의 게시글 비밀글 설정
     *
     * @param inquiry 등록할 문의 게시글 정보
     */
    void saveInquirySecretSetting(Inquiry inquiry);

    /**
     * 문의 게시글의 답변 등록
     *
     * @param answer 등록할 문의 게시글의 답변 정보
     */
    void saveAnswer(Answer answer);


    /**
     * 공지 게시글 수정
     *
     * @param notice 수정할 공지 게시글 정보
     */
    void updateNotice(Notice notice);

    /**
     * 커뮤니티 게시글 수정
     *
     * @param community 수정할 커뮤니티 게시글 정보
     */
    void updateCommunity(Community community);

    /**
     * 사진 게시글 수정
     *
     * @param photo 수정할 사진 게시물 정보
     */
    void updatePhoto(Photo photo);

    /**
     * 문의 게시글 수정
     *
     * @param inquiry 수정할 문의 게시글 정보
     */
    void updateInquiryPost(Inquiry inquiry);

    /**
     * 문의 게시글 수정
     *
     * @param inquiry 수정할 문의 게시글 정보
     */
    void updateInquiry(Inquiry inquiry);

    /**
     * 문의 게시글의 답변 수정
     *
     * @param answer 수정할 문의 게시글의 답변 정보
     */
    void updateAnswer(Answer answer);


    /**
     * 게시글 삭제
     *
     * @param postIdx 게시글 번호
     */
    void deletePost(Long postIdx);

    /**
     * 문의 테이블 삭제
     *
     * @param postIdx 게시글 번호
     */
    void deleteInquiry(Long postIdx);
}
