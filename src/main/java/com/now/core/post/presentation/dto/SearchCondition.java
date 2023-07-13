package com.now.core.post.presentation.dto;

import com.now.core.category.domain.constants.Category;
import lombok.Data;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * 검색조건을 가지고 있는 객체
 */
@Data
public class SearchCondition {

    /**
     * 시작 날짜
     */
    @Nullable
    @Pattern(regexp = "^(\\d{4})-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])$", message = "시작날짜 형식이 잘못되었습니다")
    private String startDate;

    /**
     * 종료 날짜
     */
    @Nullable
    @Pattern(regexp = "^(\\d{4})-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])$", message = "종료날짜 형식이 잘못되었습니다")
    private String endDate;

    /**
     * 카테고리
     */
    @Nullable
    private Category category;

    /**
     * 검색 키워드
     */
    @Nullable
    @Size(max = 100, message = "키워드는 100글자 이하여야 합니다")
    private String keyword;
}
