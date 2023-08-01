package com.now.common.utils;

import com.now.core.attachment.application.dto.UploadedAttachment;
import com.now.common.exception.FileInsertionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 게시글 작성 또는 수정시 서버 디렉토리에 첨부파일을 저장 또는 삭제할때 사용하는 유틸 클래스
 */
@Slf4j
public class AttachmentUtils {

    /**
     * 서버 디렉토리 저장소
     */
    public static final String UPLOAD_PATH = "C:\\git\\now\\front\\now\\public\\file\\";

    /**
     * 첨부파일 이름에서 확장자 추출을 위한 정규식 패턴
     */
    private static final String FILE_NAME_EXTENSION_REGEX = "\\.(\\w+)$";

    /**
     * 첨부파일 이름에 대한 정규식 패턴을 컴파일한 패턴 객체
     */
    public static final Pattern EXTENSION_PATTERN_COMPILE = Pattern.compile(FILE_NAME_EXTENSION_REGEX);

    /**
     * 주어진 첨부파일 이름을 인코딩하여 반환
     *
     * @param fileNameToEncoding 인코딩할 첨부파일 이름
     * @return 인코딩된 첨부파일 이름
     */
    public static String generateEncodedName(String fileNameToEncoding) {
        try {
            return URLEncoder.encode(fileNameToEncoding, "UTF-8").replaceAll("\\+", "%20");
        } catch (UnsupportedEncodingException e) {
            log.error(String.valueOf(e));
        }
        return null;
    }

    /**
     * 서버에 업로드된 첨부파일을 삭제
     *
     * @param fileNameToDelete 삭제할 첨부파일 이름
     * @return 첨부파일 삭제 성공 여부
     */
    public static boolean deleteUploadedFile(String fileNameToDelete) {
        log.debug("삭제할 File : {}{}{} ", UPLOAD_PATH,"\\",fileNameToDelete);
        return createFile(fileNameToDelete).delete();
    }

    /**
     * 서버 디렉토리에서 주어진 첨부파일명에 해당하는 첨부파일 객체를 생성하여 반환
     *
     * @param fileName 첨부파일 이름
     * @return 첨부파일 객체
     */
    private static File createFile(String fileName) {
        return new File(UPLOAD_PATH, fileName);
    }

    /**
     * 첨부파일 이름에 확장자가 있다면 확장자를 추출하여 반환하고, 그렇지 않다면 null
     *
     * @param fileName 첨부파일 이름
     * @return 확장자가 있다면 확장자를 추출하여 반환하고, 그렇지 않다면 null
     */
    public static String extractFileExtension(String fileName) {
        Matcher matcher = EXTENSION_PATTERN_COMPILE.matcher(fileName);
        if (matcher.find()) {
            return matcher.group(1).toLowerCase();
        }
        return null;
    }

    /**
     * 주어진 시스템 첨부파일명을 포함한 절대 경로를 생성
     *
     * @param systemName 시스템 첨부파일명
     * @return 절대 경로
     */
    public static Path createAbsolutePath(String systemName) {
        Path path = Paths.get(AttachmentUtils.UPLOAD_PATH + systemName);
        return path;
    }

    /**
     * 첨부파일 이름을 시스템에서 사용할 형식으로 생성하여 반환
     *
     * @param fileName 원본 첨부파일 이름
     * @return 시스템에서 사용할 첨부파일 이름
     */
    public static String generateSystemName(String fileName) {
        return String.format("%s.%s", UUID.randomUUID(), AttachmentUtils.extractFileExtension(fileName));
    }

    /**
     * 저장된 첨부파일을 바이트 배열로 변환하여 반환
     *
     * @param savedFileName 저장된 첨부파일 이름
     * @return 첨부파일의 바이트 배열
     */
    public static byte[] convertByteArray(String savedFileName) {
        ByteArrayOutputStream byteArrayOutputStream = null;
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(AttachmentUtils.createFile(savedFileName));

            byte[] buffer = new byte[1024];
            int bytesRead;

            byteArrayOutputStream = new ByteArrayOutputStream();

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }

            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            log.error(String.valueOf(e));
        } finally {
            if(byteArrayOutputStream != null) {
                try {
                    byteArrayOutputStream.close();
                } catch (IOException e) {
                    log.error(String.valueOf(e));
                }
            }
            if(inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.error(String.valueOf(e));
                }
            }
        }
        return null;
    }

    /**
     * 서버에 첨부파일을 업로드하고 {@link UploadedAttachment} 객체를 반환
     *
     * @param multipartFile 업로드할 {@link MultipartFile} 객체
     * @return 업로드된 {@link UploadedAttachment} 객체 또는 null (업로드할 첨부파일이 없는 경우)
     * @throws FileInsertionException 첨부파일 업로드 중에 에러가 발생한 경우
     */
    public static UploadedAttachment processServerUploadFile(MultipartFile multipartFile) {
        if (multipartFile.isEmpty()) {
            return null;
        }

        UploadedAttachment uploadedFile = createUploadedFileFromMultipartFile(multipartFile);

        try {
            multipartFile.transferTo(AttachmentUtils.createAbsolutePath(uploadedFile.getSystemName()));
            return uploadedFile;
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            AttachmentUtils.deleteUploadedFile(uploadedFile.getSystemName());
            throw new FileInsertionException("첨부파일 업로드 중 에러가 발생했습니다.");
        }
    }

    /**
     * {@link MultipartFile} 객체를 기반으로 {@link UploadedAttachment} 객체를 생성
     *
     * @param multipartFile 업로드할 {@link MultipartFile} 객체
     * @return 생성된 {@link UploadedAttachment} 객체
     */
    private static UploadedAttachment createUploadedFileFromMultipartFile(MultipartFile multipartFile) {
        return UploadedAttachment.builder()
                .originalAttachmentName(multipartFile.getOriginalFilename())
                .systemName(AttachmentUtils.generateSystemName(multipartFile.getOriginalFilename()))
                .attachmentSize((int) multipartFile.getSize())
                .build();
    }

    /**
     * 지정된 MultipartFile을 MultipartFile 배열의 맨 앞으로 옮기는 유틸리티 메서드
     *
     * @param multipartFile    맨 앞으로 옮길 MultipartFile
     * @param multipartFiles   대상 MultipartFile 배열
     * @return 맨 앞으로 MultipartFile을 옮긴 후의 업데이트된 MultipartFile 배열
     */
    public static MultipartFile[] moveFileToFront(MultipartFile multipartFile, MultipartFile[] multipartFiles) {
        if (multipartFile != null) {
            MultipartFile[] updatedMultipartFiles = new MultipartFile[multipartFiles != null ? multipartFiles.length + 1 : 1];
            updatedMultipartFiles[0] = multipartFile;
            if (multipartFiles != null) {
                System.arraycopy(multipartFiles, 0, updatedMultipartFiles, 1, multipartFiles.length);
            }
            multipartFiles = updatedMultipartFiles;
        }
        return multipartFiles;
    }
}
