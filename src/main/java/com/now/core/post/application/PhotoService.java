package com.now.core.post.application;

import com.now.common.exception.ErrorType;
import com.now.core.category.domain.constants.PostGroup;
import com.now.core.comment.application.CommentService;
import com.now.core.member.application.MemberService;
import com.now.core.member.domain.Member;
import com.now.core.post.domain.Photo;
import com.now.core.post.domain.PostRepository;
import com.now.core.post.exception.CannotCreatePostException;
import com.now.core.post.exception.CannotUpdatePostException;
import com.now.core.post.exception.InvalidPostException;
import com.now.core.post.presentation.dto.Condition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 사진 게시글 관련 비즈니스 로직을 처리하는 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PhotoService {

    private final PostRepository postRepository;
    private final MemberService memberService;
    private final CommentService commentService;

    /**
     * 모든 사진 게시글 정보를 조회 후 반환
     *
     * @return 사진 게시글 정보 리스트
     */
    public List<Photo> retrieveAllPhotos(Condition condition) {
        return postRepository.findAllPhotos(condition);
    }

    /**
     * 사진 게시글 응답
     *
     * @param postIdx 게시글 번호
     * @return 사진 게시글 정보
     */
    public Photo findByPostIdx(Long postIdx) {
        Photo photo = postRepository.findPhoto(postIdx);
        if (photo == null) {
            throw new InvalidPostException(ErrorType.NOT_FOUND_POST);
        }

        return photo;
    }

    /**
     * 사진 게시글 등록
     *
     * @param photo 등록할 사진 게시글 정보
     */
    public void registerPhoto(Photo photo) {
        Member member = memberService.findMemberById(photo.getMemberId());

        if (!PostGroup.isCategoryInGroup(PostGroup.PHOTO, photo.getCategory())) {
            throw new CannotCreatePostException(ErrorType.INVALID_CATEGORY);
        }

        postRepository.savePhoto(photo.updateMemberIdx(member.getMemberIdx()));
    }

    /**
     * 사진 게시글 수정
     *
     * @param photo 수정할 사진 게시글 정보
     */
    public void updatePhoto(Photo photo) {
        Member member = memberService.findMemberById(photo.getMemberId());

        if (!PostGroup.isCategoryInGroup(PostGroup.PHOTO, photo.getCategory())) {
            throw new CannotUpdatePostException(ErrorType.CAN_NOT_UPDATE_POST);
        }

        postRepository.updatePhoto(photo.updateMemberIdx(member.getMemberIdx()));
    }

    /**
     * 사진 게시글 삭제
     *
     * @param postIdx 게시글 번호
     */
    public void deletePhoto(Long postIdx) {
        postRepository.deletePhoto(postIdx);
    }

    /**
     * 게시글 수정 권한 확인
     *
     * @param postIdx  게시글 번호
     * @param memberId 회원 아이디
     */
    public void hasUpdateAccess(Long postIdx, String memberId) {
        Photo photo = postRepository.findPhoto(postIdx);
        photo.canUpdate(memberService.findMemberById(memberId));
    }

    /**
     * 게시글 삭제 권한 확인
     *
     * @param postIdx  게시글 번호
     * @param memberId 회원 아이디
     */
    public void hasDeleteAccess(Long postIdx, String memberId) {
        Photo photo = postRepository.findPhoto(postIdx);
        photo.canDelete(memberService.findMemberById(memberId), commentService.findAllByPostIdx(postIdx));
    }
}

