package com.now.core.category.presentation;

import com.now.common.mapper.EnumMapperFactory;
import com.now.common.mapper.EnumMapperValue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 카테고리 관련 작업을 위한 컨트롤러
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class CategoryController {

    private final EnumMapperFactory enumMapperFactory;

    /**
     * 모든 카테고리 정보를 조회하는 핸들러 메서드
     *
     * @return 모든 카테고리 정보와 함께 OK 응답을 반환
     */
    @GetMapping("/api/categories")
    public ResponseEntity<List<EnumMapperValue>> getAllCategories() {
        log.debug("getAllCategories 핸들러 메서드 호출");

        return ResponseEntity.ok(enumMapperFactory.get("PostGroup"));
    }
}
