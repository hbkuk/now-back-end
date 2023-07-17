package com.now.core.post.presentation.dto;

import lombok.Data;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Max;

/**
 * 게시물 제한 정보를 담는 객체
 */
@Data
public class Condition {

    @Nullable
    @Max(value = 100)
    private final Integer maxNumberOfPosts; // 게시물 개수 제한
}

