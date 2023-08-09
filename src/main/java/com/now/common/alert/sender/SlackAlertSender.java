package com.now.common.alert.sender;

import com.now.common.alert.AlertSender;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Slack으로 경고 혹은 알림 메시지를 전송하는 객체
 */
@Component
public class SlackAlertSender implements AlertSender {

    private static final String REQUEST_URI = "https://hooks.slack.com/services";

    @Value("${slack.webhook.url}")
    private String hookUri;

    /**
     * 주어진 메시지를 Slack으로 전송
     *
     * @param message 전송할 메시지 내용
     */
    @Override
    public void send(final String message) {
        WebClient.create(REQUEST_URI)
                .post()
                .uri(hookUri)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new MessageRequest(message))
                .retrieve()
                .bodyToMono(Void.class)
                .subscribe();
    }
}
