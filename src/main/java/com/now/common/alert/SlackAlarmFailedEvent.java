package com.now.common.alert;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;

@NoArgsConstructor(force = true)
@RequiredArgsConstructor
@ToString
public class SlackAlarmFailedEvent {

    private final String title;
    private final String titleLink;
    private final String email;
    private final List<String> contents;
}
