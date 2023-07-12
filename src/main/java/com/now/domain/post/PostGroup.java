package com.now.domain.post;

import com.now.code.EnumMapperType;
import com.now.exception.InvalidCategoryException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;

/**
 * 게시물 그룹을 나타내는 enum
 */
@Getter
@RequiredArgsConstructor
public enum PostGroup implements EnumMapperType {
    NOTICE(List.of(Category.EVENT, Category.NEWS)),
    COMMUNITY(List.of(Category.LIFESTYLE, Category.COMMUNITY_STUDY)),
    PHOTO(List.of(Category.DAILY_LIFE, Category.ARTWORK)),
    INQUIRY(List.of(Category.SERVICE, Category.TECHNOLOGY));

    private final List<Category> categories;

    /**
     * 전달받은 {@link Category}를 통해 게시물 그룹 찾은 후 반환, 없다면 RuntimeException 던짐
     *
     * @param category 카테고리
     * @return 해당 카테고리에 해당하는 게시물 그룹
     * @throws RuntimeException 게시물 그룹을 찾을 수 없을 경우 예외 발생
     */
    public static PostGroup findByCategory(Category category) {
        return Arrays.stream(PostGroup.values())
                .filter(postType -> postType.hasCategory(category))
                .findAny()
                .orElseThrow(InvalidCategoryException::new);
    }

    /**
     * 전달받은 카테고리와 게시물 그룹에 포함된다면 true 반환, 그렇지 않다면 false 반환
     *
     * @param category  확인할 카테고리
     * @param postGroup 확인할 게시물 그룹
     * @return 게시물 그룹에 포함된다면 true 반환, 그렇지 않다면 false 반환
     */
    public static boolean isCategoryInGroup(Category category, PostGroup postGroup) {
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

    @Override
    public String getCode() {
        return name();
    }

    @Override
    public String getTitle() {
        return categories.toString();
    }
}
