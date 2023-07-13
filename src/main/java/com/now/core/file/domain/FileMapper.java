package com.now.core.file.domain;

import org.apache.ibatis.annotations.Mapper;

/**
 * 파일 정보에 접근하는 매퍼 인터페이스
 */
@Mapper
public interface FileMapper {

    /**
     * 파일 등록
     *
     * @param file 등록할 파일 정보
     */
    void saveFile(File file);
}
