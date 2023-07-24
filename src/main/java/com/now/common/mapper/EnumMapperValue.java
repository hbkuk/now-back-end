package com.now.common.mapper;

import com.now.core.category.presentation.SubCodeGroup;
import lombok.ToString;

import java.util.List;

/**
 * {@link EnumMapperType}을 매핑하는 클래스
 */
@ToString
public class EnumMapperValue {

    private final String code;
    private final String title;
    private final List<SubCodeGroup> subCodeGroup;

    /**
     * EnumMapperValue의 생성자
     *
     * @param enumMapperType enum 매핑 타입
     */
    public EnumMapperValue(EnumMapperType enumMapperType) {
        this.code = enumMapperType.getCode();
        this.title = enumMapperType.getTitle();
        this.subCodeGroup = enumMapperType.getSubCodeGroup();
    }

    /**
     * 코드 값을 반환
     *
     * @return 코드 값
     */
    public String getCode() {
        return code;
    }

    /**
     * 제목을 반환
     *
     * @return 제목
     */
    public String getTitle() {
        return title;
    }

    public List<SubCodeGroup> getSubCodeGroup() {
        return subCodeGroup;
    }
}

