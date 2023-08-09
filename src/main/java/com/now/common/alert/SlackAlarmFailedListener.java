package com.now.common.alert;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * 알림 전송 실패 이벤트를 처리하는 리스너
 *
 * {@code @SlackLogger} 어노테이션으로 인해 발생한 알림 실패 이벤트를 로그로 기록
 */
@Component
@Slf4j
public class SlackAlarmFailedListener {

    /**
     * 알림 전송 실패 이벤트를 처리하고 로그 기록
     *
     * @param slackAlarmFailedEvent 처리할 알림 전송 실패 이벤트.
     */
    @SlackLogger
    @EventListener
    public void handle(final SlackAlarmFailedEvent slackAlarmFailedEvent) {
        log.warn("알람 전송 실패 - 내용 : {}", slackAlarmFailedEvent);
    }
}
