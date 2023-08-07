package com.now.core.post.presentation.dto;

import com.now.core.post.domain.Photo;
import com.now.core.post.presentation.dto.Page;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 사진 게시글 목록과 페이지 객체를 담는 데이터 전송 객체
 */
@Slf4j
@Builder(toBuilder = true)
@Getter
@ToString
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class PhotosResponse {

    private final List<Photo> photos;
    private final Page page;
}
