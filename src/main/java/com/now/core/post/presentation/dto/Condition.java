package com.now.core.post.presentation.dto;

import lombok.Data;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Size;

/**
 * 게시물 제한 정보를 담는 객체
 */
@Data
public class Condition {

    @Nullable
    @Size(max = 100)
    private final int maxNumberOfPosts; // 게시물 개수 제한
}

