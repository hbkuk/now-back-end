package com.now.common.config.infrastructure;

/**
 * Rate Limiting 관련 헤더 이름을 정의하는 클래스
 */
public class RateLimitHeaders {

    /**
     * Retry After 헤더 이름
     */
    public static final String RETRY_AFTER = "X-RateLimit-Retry-After";

    private RateLimitHeaders() { // 이 클래스는 인스턴스화를 방지하기 위해 private 생성자
    }
}
