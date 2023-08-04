package com.now.core.attachment.exception;

import com.now.common.exception.BadRequestException;
import com.now.common.exception.ErrorType;

/**
 * 대표 이미지(썸네일) 업데이트가 불가능한 경우 발생
 */
public class CannotUpdateThumbnailException extends BadRequestException {

    public CannotUpdateThumbnailException(ErrorType errorType) {
        super(errorType);
    }
}
