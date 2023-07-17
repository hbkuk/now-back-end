package com.now.core.member.exception;

import com.now.common.exception.BadRequestException;
import com.now.common.exception.ErrorType;

/**
 * 중복되는 회원 정보가 있다면 던져지는 Unchecked Exception.
 */
public class DuplicateMemberInfoException extends BadRequestException {

    public DuplicateMemberInfoException(final ErrorType errorType) {
        super(errorType);
    }
}
