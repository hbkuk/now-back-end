package com.now.core.post.domain;

import com.now.core.post.presentation.dto.Answer;
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
     * 공지 게시글 정보를 조회 후 반환
     * @param postIdx 게시글 번호
     * @return 공지 게시글 정보
     */
    Notice findNotice(Long postIdx);

    /**
     * 커뮤니티 게시글 정보를 조회 후 반환
     * @param postIdx 게시글 번호
     * @return 커뮤니티 게시글 정보
     */
    Community findCommunity(Long postIdx);

    /**
     * 사진 게시글 정보를 조회 후 반환
     * @param postIdx 게시글 번호
     * @return 사진 게시글 정보
     */
    Photo findPhoto(Long postIdx);

    /**
     * 문의 게시글 정보를 조회 후 반환
     * @param postIdx 게시글 번호
     * @return 문의 게시글 정보
     */
    Inquiry findInquiry(Long postIdx);

    //TODO: 상속을 통한, 제네릭을 통한 <? extends ManagerPost>, <? extends MemberPost> 타입 제한
    //void saveManagerPost(Object object);
    //void saveMemberPost(Object object);

    /**
     * 공지 게시글 등록
     *
     * @param notice  등록할 공지 게시글 정보
     */
    void saveNotice(Notice notice);

    /**
     * 커뮤니티 게시글 등록
     *
     * @param community  할 커뮤니티 게시글 정보
     */
    void saveCommunity(Community community);

    /**
     * 사진 게시글 등록
     *
     * @param photo  등록할 공지 게시글 정보
     */
    void savePhoto(Photo photo);

    /**
     * 문의 게시글 등록
     *
     * @param inquiry  등록할 공지 게시글 정보
     */
    void saveInquiry(Inquiry inquiry);

    /**
     * 문의 게시글 비밀글 설정
     *
     * @param inquiry  등록할 공지 게시글 정보
     */
    void saveInquirySecretSetting(Inquiry inquiry);

    /**
     * 문의 게시글의 답변 게시글 등록
     *
     * @param answer  등록할 공지 게시글 정보
     */
    void saveAnswer(Answer answer);
}
