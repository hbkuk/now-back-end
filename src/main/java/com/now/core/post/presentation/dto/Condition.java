package com.now.core.post.presentation.dto;

import lombok.Data;
import org.springframework.lang.Nullable;

/**
 * Limit 정보를 담는 객체
 */
@Data
public class Condition {

    @Nullable
    private final int limit;
}
