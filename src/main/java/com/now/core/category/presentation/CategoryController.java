package com.now.core.category.presentation;

import com.now.common.mapper.EnumMapperValue;
import com.now.core.category.domain.constants.PostGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 카테고리 관련 작업을 위한 컨트롤러
 */
@Slf4j
@RestController
public class CategoryController {

    /**
     * 모든 카테고리 정보를 조회하는 핸들러 메서드
     *
     * @return 모든 카테고리 정보와 함께 OK 응답을 반환
     */
    @GetMapping("/api/categories")
    public ResponseEntity<List<EnumMapperValue>> findCategories() {
        log.debug("findCategories 핸들러 메서드 호출");

        List<EnumMapperValue> categories = Arrays.stream(PostGroup.values())
                .map(EnumMapperValue::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(categories);
    }
}
