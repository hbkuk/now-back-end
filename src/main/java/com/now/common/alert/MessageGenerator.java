package com.now.common.alert;

import lombok.NoArgsConstructor;

/**
 * 메시지를 생성하는 유틸리티
 */
@NoArgsConstructor
public class MessageGenerator {

    private static final String USER_TEMPLATE = "USER_ID = %s\n";
    private static final String EXCEPTION_TITLE = "[ EXCEPTION ]\n";
    private static final String ALARM_FAILED_TITLE = "[ ALARM FAILED ]\n";
    private static final String EXCEPTION_TEMPLATE = "%s %s %s (line : %d)";

    /**
     * 사용자와 예외 정보를 기반으로 로그 메시지 생성
     *
     * @param user            사용자 정보
     * @param exceptionWrapper 예외 정보 객체
     * @return 생성된 로그 메시지
     */
    public static String generate(final String user, final ExceptionWrapper exceptionWrapper) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.format(USER_TEMPLATE, user));
        exceptionAppender(exceptionWrapper, stringBuilder);
        return stringBuilder.toString();
    }

    /**
     * 알림 실패 이벤트를 기반으로 알림 실패 메시지를 생성
     *
     * @param slackAlarmFailedEvent 알림 실패 이벤트 객체
     * @return 생성된 알림 실패 메시지
     */
    public static String generateFailedAlarmMessage(final SlackAlarmFailedEvent slackAlarmFailedEvent) {
        return ALARM_FAILED_TITLE + slackAlarmFailedEvent;
    }

    /**
     * 기존 문자열 빌더에 예외 정보를 추가 후 반환
     *
     * @param exceptionWrapper 예외 정보 객체
     * @param stringBuilder    기존 문자열 빌더에 추가할 문자열 생성
     * @return 기존 문자열 빌더에 예외 정보를 추가 후 반환
     */
    private static StringBuilder exceptionAppender(final ExceptionWrapper exceptionWrapper,
                                                   final StringBuilder stringBuilder) {
        return stringBuilder.append(EXCEPTION_TITLE)
                .append(String.format(EXCEPTION_TEMPLATE, exceptionWrapper.getExceptionClassName(),
                        exceptionWrapper.getExceptionMethodName(),
                        exceptionWrapper.getMessage(),
                        exceptionWrapper.getExceptionLineNumber()));
    }
}
