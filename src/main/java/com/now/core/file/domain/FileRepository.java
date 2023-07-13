package com.now.core.file.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * 파일 관련 정보를 관리하는 레포지토리
 */
@Repository
public class FileRepository {

    public final FileMapper fileMapper;

    @Autowired
    public FileRepository(FileMapper fileMapper) {
        this.fileMapper = fileMapper;
    }
    
    /**
     * 파일 저장
     *
     * @param file 저장할 파일 정보
     */
    public void saveFile(File file) {
        fileMapper.saveFile(file);
    }
}
