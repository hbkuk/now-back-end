package com.now.core.category.domain.constants;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.now.common.exception.ErrorType;
import com.now.common.mapper.EnumMapperType;
import com.now.core.category.exception.InvalidCategoryException;
import com.now.core.category.presentation.SubCodeGroup;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 게시물 그룹을 나타내는 enum
 */
@Getter
@RequiredArgsConstructor
public enum PostGroup implements EnumMapperType {
    NOTICE("공지", List.of(Category.EVENT, Category.NEWS)),
    COMMUNITY("커뮤니티", List.of(Category.LIFESTYLE, Category.COMMUNITY_STUDY)),
    PHOTO("사진", List.of(Category.DAILY_LIFE, Category.ARTWORK)),
    INQUIRY("문의", List.of(Category.SERVICE, Category.TECHNOLOGY));

    private final String title;
    private final List<Category> categories;

    /**
     * 전달받은 {@link Category}를 통해 게시물 그룹 찾은 후 반환, 없다면 InvalidCategoryException 던짐
     *
     * @param category 카테고리
     * @return 해당 카테고리에 해당하는 게시물 그룹
     * @throws InvalidCategoryException 게시물 그룹을 찾을 수 없을 경우 예외 발생
     */
    public static PostGroup findByCategory(Category category) {
        return Arrays.stream(PostGroup.values())
                .filter(postType -> postType.hasCategory(category))
                .findAny()
                .orElseThrow(() -> new InvalidCategoryException(ErrorType.NOT_FOUND_CATEGORY));
    }

    /**
     * 전달받은 카테고리와 게시물 그룹에 포함된다면 true 반환, 그렇지 않다면 false 반환
     *
     * @param postGroup 확인할 게시물 그룹
     * @param category  확인할 카테고리
     * @return 게시물 그룹에 포함된다면 true 반환, 그렇지 않다면 false 반환
     */
    public static boolean isCategoryInGroup(PostGroup postGroup, Category category) {
        return PostGroup.findByCategory(category) == postGroup;
    }

    /**
     * 전달받은 카테고리가 현재 게시물 그룹에 속한다면 true 반환, 그렇지 않다면 false 반환
     *
     * @param category 확인할 카테고리
     * @return 카테고리가 속하는 경우 true, 그렇지 않은 경우 false
     */
    private boolean hasCategory(Category category) {
        return categories.stream()
                .anyMatch(element -> element.equals(category));
    }

    /**
     * 전달받은 값으로부터 해당하는 PostGroup enum 생성
     *
     * @param value PostGroup name에 해당하는 값
     * @return 전달받은 값으로부터 해당하는 PostGroup enum
     */
    @JsonCreator //  JSON 값을 열거형으로 변환할 때 사용
    public static PostGroup from(String value) {
        for (PostGroup postGroup : PostGroup.values()) {
            if (postGroup.name().equals(value)) {
                return postGroup;
            }
        }
        return null;
    }

    /**
     * enum 상수의 코드 값 반환
     *
     * @return 코드 값
     */
    @Override
    public String getCode() {
        return name();
    }

    /**
     * enum 상수의 제목 반환
     *
     * @return 제목
     */
    @Override
    public String getTitle() {
        return title;
    }

    /**
     * enum 상수의 하위 코드 반환
     *
     * @return 제목
     */
    public List<SubCodeGroup> getSubCodeGroup() {
        return categories.stream()
                .map(category -> new SubCodeGroup(category.name(), category.getTitle()))
                .collect(Collectors.toList());
    }
}
