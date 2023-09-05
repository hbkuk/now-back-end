package com.now.core.attachment.application;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.now.common.exception.FileInsertionException;
import com.now.core.attachment.application.dto.UploadedAttachment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

import static com.now.common.utils.AttachmentUtils.createUploadedFileFromMultipartFile;

/**
 * Amazon S3 기반 첨부파일 업로드 및 삭제하는 서비스
 */
@Slf4j
@Service
@Profile("prod")
@RequiredArgsConstructor
public class AmazonCloudStorageService implements StorageService {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Override
    public UploadedAttachment upload(MultipartFile multipartFile) {
        UploadedAttachment uploadedFile = createUploadedFileFromMultipartFile(multipartFile);
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(uploadedFile.getExtension());

            amazonS3.putObject(new PutObjectRequest(bucket, uploadedFile.getSystemName(),
                    multipartFile.getInputStream(), metadata));

            return uploadedFile;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            delete(uploadedFile.getSystemName());
            throw new FileInsertionException("첨부파일 업로드 중 에러가 발생했습니다.");
        }
    }

    @Override
    public boolean delete(String attachmentName) {
        try {
            // Amazon S3에서 파일 삭제
            amazonS3.deleteObject(bucket, attachmentName);
            return true;
        } catch (AmazonS3Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public InputStream createStream(String attachmentName) {
        // Amazon S3에서 스트림 생성
        S3Object object = amazonS3.getObject(bucket, attachmentName);
        return object.getObjectContent();
    }
}
