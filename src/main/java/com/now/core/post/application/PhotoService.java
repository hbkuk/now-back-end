package com.now.core.post.application;

import com.now.core.authentication.constants.Authority;
import com.now.core.category.domain.constants.PostGroup;
import com.now.core.member.application.MemberService;
import com.now.core.member.domain.Member;
import com.now.core.post.domain.Photo;
import com.now.core.post.domain.PostRepository;
import com.now.core.post.exception.CannotWritePostException;
import com.now.core.post.exception.PermissionDeniedException;
import com.now.core.post.presentation.dto.Condition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * 사진 게시글 관련 비즈니스 로직을 처리하는 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PhotoService {

    private final PostRepository postRepository;
    private final MemberService memberService;
    private final MessageSourceAccessor messageSource;

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
        if(photo == null) {
            throw new NoSuchElementException(messageSource.getMessage("error.noSuch.post"));
        }

        return photo;
    }

    /**
     * 사진 게시글 등록
     *
     * @param photo     등록할 사진 게시글 정보
     * @param authority 권한
     * @throws PermissionDeniedException 권한이 없을 경우 발생하는 예외
     * @throws CannotWritePostException  게시글 작성이 불가능한 경우 발생하는 예외
     */
    public void registerPhoto(Photo photo, Authority authority) {
        if (authority != Authority.MEMBER) {
            throw new PermissionDeniedException(messageSource.getMessage("error.permission.denied"));
        }

        if (!PostGroup.isCategoryInGroup(photo.getCategory(), photo.getPostGroup())) {
            throw new CannotWritePostException(messageSource.getMessage("error.write.failed"));
        }

        Member member = memberService.findMemberById(photo.getMemberId());

        postRepository.savePhoto(photo.updateMemberIdx(member.getMemberIdx()));
    }
}

