package com.now.common.config.infrastructure;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import io.github.bucket4j.local.LocalBucketBuilder;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

/**
 * Rate Limiting 관련 객체를 생성하는 팩토리 클래스
 */
@Component
public class RateLimitingFactory {

    /**
     * 주어진 최대 대역폭과 토큰 리필 지속 시간으로 버킷 생성
     *
     * @param maxBandwidth               최대 대역폭
     * @param tokenRefillCount           토큰 리필 횟수
     * @param tokenRefillDurationMinutes 토큰 리필 지속 시간(분)
     * @return
     */
    public Bucket generateBucket(int maxBandwidth, int tokenRefillCount, int tokenRefillDurationMinutes) {
        return Bucket.builder()
                .addLimit(
                    getClassicBandwidth(maxBandwidth,
                    getIntervalRefill(tokenRefillCount, Duration.ofMinutes(tokenRefillDurationMinutes))))
                .build();
    }

    /**
     * 주어진 대역폭 목록으로 버킷 생성
     *
     * @param bandwidthList 대역폭 목록
     * @return 생성된 버킷 객체
     */
    public Bucket generateComplexBucket(List<Bandwidth> bandwidthList) {
        LocalBucketBuilder bucketBuilder = Bucket.builder();
        for (Bandwidth bandwidth : bandwidthList) {
            bucketBuilder.addLimit(bandwidth);
        }
        return bucketBuilder.build();
    }

    /**
     * 최대 대역폭과 토큰 리필 지속 시간으로 대역폭을 생성
     *
     * @param maxBandwidth               최대 대역폭
     * @param tokenRefillDurationMinutes 토큰 리필 지속 시간(분)
     * @return 생성된 대역폭 객체
     */
    public Bandwidth getSimpleBandwidth(int maxBandwidth, int tokenRefillDurationMinutes) {
        return Bandwidth.simple(maxBandwidth, Duration.ofMinutes(tokenRefillDurationMinutes));
    }

    /**
     * 주어진 최대 대역폭과 Refill 객체로 대역폭 생성
     *
     * @param maxBandwidth 최대 대역폭
     * @param refill       Refill 객체
     * @return 생성된 대역폭 객체
     */
    public Bandwidth getClassicBandwidth(int maxBandwidth, Refill refill) {
        return Bandwidth.classic(maxBandwidth, refill);
    }

    /**
     * 주어진 토큰 리필 횟수와 토큰 리필 간격으로 Refill 객체 생성
     *
     * @param tokenRefillCount 토큰 리필 횟수
     * @param duration         토큰 리필 간격
     * @return 생성된 Refill 객체
     */
    public Refill getIntervalRefill(int tokenRefillCount, Duration duration) {
        return Refill.intervally(tokenRefillCount, duration);
    }

    /**
     * 주어진 토큰 리필 횟수와 토큰 리필 지속 시간으로 Refill 객체 생성
     *
     * @param tokenRefillCount           토큰 리필 횟수
     * @param tokenRefillDurationMinutes 토큰 리필 지속 시간(분)
     * @return 생성된 Refill 객체
     */
    public Refill getGreedyRefill(int tokenRefillCount, int tokenRefillDurationMinutes) {
        return Refill.greedy(tokenRefillCount, Duration.ofMinutes(tokenRefillDurationMinutes));
    }
}

