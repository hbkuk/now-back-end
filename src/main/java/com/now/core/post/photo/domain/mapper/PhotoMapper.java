package com.now.core.post.photo.domain.mapper;

import com.now.core.post.photo.domain.Photo;
import com.now.core.post.common.presentation.dto.Condition;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 사진 게시글 정보에 접근하는 매퍼 인터페이스
 */
@Mapper
public interface PhotoMapper {

    /**
     * 모든 사진 게시글 정보를 조회 후 반환
     *
     * @param condition 게시물 제한 정보를 담은 객체
     * @return 사진 게시글 정보 리스트
     */
    List<Photo> findAllPhotos(Condition condition);


    /**
     * 사진 게시글 정보를 조회 후 반환
     *
     * @param postIdx 게시글 번호
     * @return 사진 게시글 정보
     */
    Photo findPhoto(Long postIdx);


    /**
     * 사진 게시글 등록
     *
     * @param photo 등록할 사진 게시글 정보
     */
    void savePhoto(Photo photo);


    /**
     * 사진 게시글 수정
     *
     * @param photo 수정할 사진 게시물 정보
     */
    void updatePhoto(Photo photo);

    /**
     * 사진 게시글 삭제
     *
     * @param postIdx 게시글 번호
     */
    void deletePhoto(Long postIdx);
}
