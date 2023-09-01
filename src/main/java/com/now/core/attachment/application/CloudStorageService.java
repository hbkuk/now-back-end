package com.now.core.attachment.application;

import com.google.cloud.ReadChannel;
import com.google.cloud.storage.*;
import com.now.common.exception.FileInsertionException;
import com.now.common.utils.AttachmentUtils;
import com.now.core.attachment.application.dto.UploadedAttachment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;

import static com.now.common.utils.AttachmentUtils.createUploadedFileFromMultipartFile;

/**
 * Cloud Storage기반 첨부파일 업로드 및 삭제하는 서비스
 */
@Slf4j
@Service
@Profile({"prod"})
public class CloudStorageService implements StorageService {

    @Value("${spring.cloud.gcp.storage.project-id}")
    private String projectId;

    @Value("${spring.cloud.gcp.storage.bucket-name}")
    private String bucketName;

    @Override
    public UploadedAttachment upload(MultipartFile multipartFile) {
        UploadedAttachment uploadedFile = createUploadedFileFromMultipartFile(multipartFile);
        try {
            Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
            BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, uploadedFile.getSystemName())
                    .setContentType(uploadedFile.getExtension())
                    .build();
            storage.createFrom(blobInfo, multipartFile.getInputStream());
            return uploadedFile;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            delete(uploadedFile.getSystemName());
            throw new FileInsertionException("첨부파일 업로드 중 에러가 발생했습니다.");
        }
    }

    @Override
    public boolean delete(String attachmentName) {
        Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
        Blob blob = storage.get(bucketName, attachmentName);
        if (blob == null) {
            return true;
        }
        Storage.BlobSourceOption precondition =
                Storage.BlobSourceOption.generationMatch(blob.getGeneration());
        storage.delete(bucketName, attachmentName, precondition);
        return true;
    }

    @Override
    public InputStream createStream(String attachmentName) throws IOException {
        Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
        BlobId blobId = BlobId.of(bucketName, attachmentName);
        ReadChannel reader = storage.get(blobId).reader();
        return Channels.newInputStream(reader);
    }
}
