package com.now.core.attachment.exception;

import com.now.common.exception.BadRequestException;
import com.now.common.exception.ErrorType;

public class InvalidAttachmentException extends BadRequestException {

    public InvalidAttachmentException(ErrorType errorType) {
        super(errorType);
    }
}
