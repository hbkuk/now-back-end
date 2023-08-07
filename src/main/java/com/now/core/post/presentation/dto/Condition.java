package com.now.core.post.presentation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.now.core.category.domain.constants.Category;
import com.now.core.category.domain.constants.PostGroup;
import com.now.core.post.presentation.dto.constants.Sort;
import lombok.Data;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * 조건 정보를 담는 데이터 전송 객체
 */
@Data
public class Condition {

    @Nullable
    @Pattern(regexp = "^(\\d{4})-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])$", message = "{condition.date.pattern}")
    private String startDate; // 시작 날짜

    @Nullable
    @Pattern(regexp = "^(\\d{4})-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])$", message = "{condition.date.pattern}")
    private String endDate; // 종료 날짜

    @Nullable
    private PostGroup postGroup; // 게시물 그룹

    @Nullable
    private Category category; // 카테고리

    @Nullable
    @Size(max = 20, message = "{condition.keyword.size}")
    private String keyword; // 키워드

    @Nullable
    @Max(value = 30, message = "{condition.maxNum.size}")
    private Integer maxNumberOfPosts; // 게시물 개수 제한

    @Nullable
    private Sort sort; // 정렬

    @Nullable
    @Min(value = 1, message = "{condition.pageNo.size}")
    private Integer pageNo; // 페이지 번호

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Page page; // 페이지 객체

    /**
     * 현재 필드의 Page 객체 업데이트
     *
     * @return 업데이트된 Condition 객체
     */
    public Condition updatePage() {
        this.page = Page.of(this.pageNo);
        return this;
    }
}

