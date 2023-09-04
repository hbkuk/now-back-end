package com.now.core.attachment.application;

import com.now.common.exception.FileInsertionException;
import com.now.common.utils.AttachmentUtils;
import com.now.core.attachment.application.dto.UploadedAttachment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static com.now.common.utils.AttachmentUtils.createFile;
import static com.now.common.utils.AttachmentUtils.createUploadedFileFromMultipartFile;

/**
 * 로컬 환경에서 첨부파일 업로드 및 삭제하는 서비스
 */
@Slf4j
@Service
@Profile({"test", "local", "dev"})
public class LocalStorageService implements StorageService {

    @Override
    public UploadedAttachment upload(MultipartFile multipartFile) {
        UploadedAttachment uploadedFile = createUploadedFileFromMultipartFile(multipartFile);
        try {
            multipartFile.transferTo(AttachmentUtils.createAbsolutePath(uploadedFile.getSystemName()));
            return uploadedFile;
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            delete(uploadedFile.getSystemName());
            throw new FileInsertionException("첨부파일 업로드 중 에러가 발생했습니다.");
        }
    }

    @Override
    public boolean delete(String attachmentName) {
        return createFile(attachmentName).delete();
    }

    @Override
    public InputStream createStream(String attachmentName) throws IOException {
        return new FileInputStream(AttachmentUtils.createFile(attachmentName));
    }
}
