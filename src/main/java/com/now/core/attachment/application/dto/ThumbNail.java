package com.now.core.attachment.application.dto;

import lombok.*;

/**
 * 대표이미지 정보를 담고 있는 개체
 */
@Builder(toBuilder = true)
@ToString
@Getter
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class ThumbNail {

    private final Long thumbNailIdx;
    private final Long memberPostIdx;
    private final Long attachmentIdx;
}
