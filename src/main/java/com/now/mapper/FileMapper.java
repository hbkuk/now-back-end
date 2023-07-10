package com.now.mapper;

import com.now.domain.file.File;
import org.apache.ibatis.annotations.Mapper;

/**
 * 파일 정보에 접근하는 매퍼 인터페이스
 *
 * @Mapper
 * : MyBatis의 매퍼 인터페이스임을 나타냄.
 */
@Mapper
public interface FileMapper {

    /**
     * 파일 등록
     *
     * @param file 등록할 파일 정보
     */
    void insert(File file);
}
