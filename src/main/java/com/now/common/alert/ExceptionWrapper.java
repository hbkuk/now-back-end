package com.now.common.alert;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 예외 정보 포장 객체
 */
@Getter
@RequiredArgsConstructor
public class ExceptionWrapper {

    private final String exceptionClassName;
    private final String exceptionMethodName;
    private final int exceptionLineNumber;
    private final String message;

    /**
     * 주어진 예외 객체에서 예외 정보를 추출 후 래핑된 예외 객체를 생성 후 반환
     *
     * @param calledException 추출할 예외 객체
     * @return 주어진 예외 객체에서 예외 정보를 추출 후 래핑된 예외 객체를 생성 후 반환
     */
    public static ExceptionWrapper extractExceptionWrapper(final Exception calledException) {
        StackTraceElement[] exceptionStackTrace = calledException.getStackTrace();
        String exceptionClassName = exceptionStackTrace[0].getClassName();
        String exceptionMethodName = exceptionStackTrace[0].getMethodName();
        int exceptionLineNumber = exceptionStackTrace[0].getLineNumber();
        String message = calledException.getMessage();

        return new ExceptionWrapper(exceptionClassName, exceptionMethodName, exceptionLineNumber, message);
    }
}
