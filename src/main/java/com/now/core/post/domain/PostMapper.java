package com.now.core.post.domain;

import com.now.core.post.presentation.dto.Answer;
import com.now.core.post.presentation.dto.Condition;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

//TODO: 상속을 통한, 제네릭을 통한 <? extends ManagerPost>, <? extends MemberPost> 타입 제한
/**
 * 게시글 정보에 접근하는 매퍼 인터페이스
 */
@Mapper
public interface PostMapper {

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
     * 공지 게시글 등록
     *
     * @param notice  등록할 공지 게시글 정보
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
     * @param photo  등록할 사진 게시글 정보
     */
    void savePhoto(Photo photo);

    /**
     * 문의 게시글 등록
     *
     * @param inquiry  등록할 문의 게시글 정보
     */
    void saveInquiry(Inquiry inquiry);

    /**
     * 문의 게시글 비밀글 설정
     *
     * @param inquiry  등록할 문의 게시글 정보
     */
    void saveInquirySecretSetting(Inquiry inquiry);

    /**
     * 문의 게시글의 답변 등록
     *
     * @param answer  등록할 문의 게시글의 답변 정보
     */
    void saveAnswer(Answer answer);




    /**
     * 공지 게시글 수정
     *
     * @param notice 수정할 공지 게시글 정보
     */
    void updateManageNoticePost(Notice notice);

    /**
     * 커뮤니티 게시글 수정
     *
     * @param community 수정할 커뮤니티 게시글 정보
     */
    void updateMemberCommunityPost(Community community);

    /**
     * 사진 게시글 수정
     *
     * @param photo 수정할 사진 게시물 정보
     */
    void updateMemberPhotoPost(Photo photo);

    /**
     * 문의 게시글 수정
     *
     * @param inquiry 수정할 문의 게시글 정보
     */
    void updateMemberInquiryPost(Inquiry inquiry);

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
     * 매니저 작성 게시글 삭제
     *
     * @param postIdx 게시글 번호
     */
    void deleteManagerPost(Long postIdx);

    /**
     * 회원 작성 게시글 삭제
     *
     * @param postIdx 게시글 번호
     */
    void deleteMemberPost(Long postIdx);

    /**
     * 문의 테이블 삭제
     *
     * @param postIdx 게시글 번호
     */
    void deleteInquiry(Long postIdx);
}
