package com.now.service;

import com.now.domain.file.*;
import com.now.exception.FileInsertionException;
import com.now.repository.FileRepository;
import com.now.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
     * 업로드할 파일이 있는지 확인
     *
     * @param multipartFiles 업로드할 파일 배열
     * @return 업로드할 파일 여부
     */
    private static boolean hasExistUploadFile(MultipartFile[] multipartFiles) {
        return multipartFiles != null && multipartFiles.length > 0;
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
     * @param multipartFiles    multipartFile[] 객체
     * @param postIdx           게시글 번호
     * @param uploadType        파일 업로드 타입
     */
    public void insert(MultipartFile[] multipartFiles, Long postIdx, UploadType uploadType) {
        if (hasExistUploadFile(multipartFiles)) {
            List<File> files = processServerUploadedFiles(multipartFiles, uploadType);
            files.forEach(file -> insert(file.updatePostIdx(postIdx)));
        }
    }

    /**
     * 파일을 서버 디렉토리에 업로드 후 {@link File} 목록을 반환
     *
     * @param multipartFiles 업로드할 {@link MultipartFile} 배열
     * @return {@link File} 목록
     */
    public List<File> processServerUploadedFiles(MultipartFile[] multipartFiles, UploadType uploadType) {
        return Arrays.stream(multipartFiles)
                .limit(uploadType.getMaxUploadCount())
                .map(multipartFile -> processServerUploadFile(multipartFile, uploadType))
                .takeWhile(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * 서버에 디렉토리에 업로드 후 {@link File} 객체 반환
     *
     * @param multipartFile   업로드할 {@link MultipartFile} 객체
     * @param uploadType    파일 크기 타입와 확장자 타입 정보를 가지고있는 enum
     * @return 업로드된 {@link File} 객체 (File) 또는 null (업로드할 파일이 없는 경우)
     * @throws FileInsertionException 파일 업로드 중에 에러가 발생한 경우
     */
    private File processServerUploadFile(MultipartFile multipartFile, UploadType uploadType) {
        if (multipartFile.isEmpty()) {
            return null;
        }

        String fileName = multipartFile.getOriginalFilename();
        String systemName = FileUtils.generateSystemName(fileName);

        File file = null;
        try {
            multipartFile.transferTo(FileUtils.createAbsolutePath(systemName));
            file = File.builder()
                    .savedFileName(systemName)
                    .originalFileName(new OriginalFileName(fileName))
                    .fileExtension(new FileExtension(FileUtils.extractFileExtension(fileName), uploadType.getAllowedExtensions()))
                    .fileSize(new FileSize((int) multipartFile.getSize(), uploadType.getMaxUploadSize()))
                    .build();
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage(), e);
            FileUtils.deleteUploadedFile(systemName);
            return null;
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            FileUtils.deleteUploadedFile(systemName);
            throw new FileInsertionException("파일 업로드 중 에러가 발생했습니다.");
        }
        return file;
    }

    /**
     * 서버 디렉토리에서 파일을 삭제
     *
     * @param files 삭제할 파일 목록
     */
    public void deleteFilesFromServerDirectory(List<File> files) {
        files.forEach(file -> FileUtils.deleteUploadedFile(file.getSavedFileName()));
        files.clear(); // 파일 삭제 후 리스트 비우기
    }
}

