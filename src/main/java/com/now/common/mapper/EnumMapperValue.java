package com.now.common.mapper;

/**
 * {@link EnumMapperType}을 매핑하는 클래스
 */
public class EnumMapperValue {

    private String code;
    private String title;

    /**
     * EnumMapperValue의 생성자
     *
     * @param enumMapperType enum 매핑 타입
     */
    public EnumMapperValue(EnumMapperType enumMapperType) {
        this.code = enumMapperType.getCode();
        this.title = enumMapperType.getTitle();
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

    /**
     * 객체의 문자열 표현을 반환
     *
     * @return 문자열 표현
     */
    @Override
    public String toString() {
        return "{" +
                "code='" + code + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}

