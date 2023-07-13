package com.now.common.mapper;


/**
 * {@link Enum}을 매핑하는 인터페이스
 */
public interface EnumMapperType {

    /**
     * enum 상수의 코드 값 반환
     *
     * @return 코드 값
     */
    String getCode();

    /**
     * enum 상수의 제목 반환
     *
     * @return 제목
     */
    String getTitle();
}

