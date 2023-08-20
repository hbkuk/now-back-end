package com.now.core.post.photo.domain.repository;

import com.now.core.post.photo.domain.Photo;
import com.now.core.post.photo.domain.mapper.PhotoMapper;
import com.now.core.post.common.presentation.dto.Condition;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 사진 게시글 관련 정보를 관리하는 레포지토리
 */
@Repository
@RequiredArgsConstructor
public class PhotoRepository {

    private final PhotoMapper photomapper;

    /**
     * 모든 사진 게시글 정보를 조회 후 반환
     *
     * @param condition 게시물 제한 정보를 담은 객체
     * @return 사진 게시글 정보 리스트
     */
    public List<Photo> findAllPhotos(Condition condition) {
        return photomapper.findAllPhotos(condition);
    }

    /**
     * 사진 게시글 정보를 조회 후 반환
     *
     * @param postIdx 게시글 번호
     * @return 사진 게시글 정보
     */
    public Photo findPhoto(Long postIdx) {
        return photomapper.findPhoto(postIdx);
    }

    /**
     * 사진 게시글 등록
     *
     * @param photo 등록할 사진 게시글 정보
     */
    public void savePhoto(Photo photo) {
        photomapper.savePhoto(photo);
    }


    /**
     * 사진 게시글 수정
     *
     * @param photo 수정할 사진 게시물 정보
     */
    public void updatePhoto(Photo photo) {
        photomapper.updatePhoto(photo);
    }

    /**
     * 사진 게시글 삭제
     *
     * @param postIdx 삭제할 게시글 번호
     */
    public void deletePhoto(Long postIdx) {
        photomapper.deletePhoto(postIdx);
    }
}
