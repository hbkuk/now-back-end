package com.now.service;

import com.now.domain.file.*;
import com.now.dto.UploadedFile;
import com.now.repository.FileRepository;
import com.now.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 파일 관련 비즈니스 로직을 처리하는 서비스
 */
@Slf4j
@Service
public class FileService {

    private final FileRepository fileRepository;

    @Autowired
    public FileService(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    /**
     * 데이터베이스에 파일 저장
     *
     * @param file 파일 정보가 담긴 객체
     */
    public void insert(File file) {
        fileRepository.insert(file);
    }

    /**
     * 파일을 서버 디렉토리에 업로드 후 데이터베이스에 파일 저장
     *
     * @param multipartFiles multipartFile[] 객체
     * @param postIdx        게시글 번호
     * @param uploadType     파일 업로드 타입
     */
    public void insert(MultipartFile[] multipartFiles, Long postIdx, UploadType uploadType) {
        List<File> files = uploadedFile(multipartFiles, uploadType);
        files.forEach(file -> insert(file.updatePostIdx(postIdx)));
    }

    /**
     * 파일을 업로드 하고, 업로드된 파일 목록을 반환
     *
     * @param multipartFiles 업로드할 {@link MultipartFile} 배열
     * @param uploadType     파일 업로드 타입을 정의한 {@link UploadType} 객체
     * @return 업로드된 파일 목록
     */
    public List<File> uploadedFile(MultipartFile[] multipartFiles, UploadType uploadType) {
        return Arrays.stream(multipartFiles)
                .limit(uploadType.getMaxUploadCount())
                .map(multipartFile -> convertToFile(FileUtils.processServerUploadFile(multipartFile), uploadType))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * {@link UploadedFile} 객체를 {@link File} 객체로 변환
     *
     * @param uploadedFile 업로드된 파일 정보를 담고 있는 {@link UploadedFile} 객체
     * @param uploadType   파일 업로드 타입을 정의한 {@link UploadType} 객체
     * @return 변환된 {@link File} 객체
     */
    private File convertToFile(UploadedFile uploadedFile, UploadType uploadType) {
        if (uploadedFile == null) {
            return null;
        }

        try {
            return File.builder()
                    .savedFileName(uploadedFile.getSystemName())
                    .originalFileName(new OriginalFileName(uploadedFile.getOriginalFileName()))
                    .fileExtension(new FileExtension(FileUtils.extractFileExtension(uploadedFile.getSystemName()), uploadType.getAllowedExtensions()))
                    .fileSize(new FileSize(uploadedFile.getFileSize(), uploadType.getMaxUploadSize()))
                    .build();
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage(), e);
            FileUtils.deleteUploadedFile(uploadedFile.getSystemName());
            return null;
        }
    }
}

