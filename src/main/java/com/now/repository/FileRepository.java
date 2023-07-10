package com.now.repository;

import com.now.domain.file.File;
import com.now.mapper.FileMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * 파일 관련 정보를 관리하는 리포지토리
 */
@Repository
public class FileRepository {

    public final FileMapper fileMapper;

    @Autowired
    public FileRepository(FileMapper fileMapper) {
        this.fileMapper = fileMapper;
    }
    
    /**
     * 파일 등록
     *
     * @param file 등록할 파일 정보
     */
    public void insert(File file) {
        fileMapper.insert(file);
    }
}
