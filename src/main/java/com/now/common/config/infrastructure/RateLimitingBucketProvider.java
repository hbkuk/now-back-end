package com.now.common.config.infrastructure;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class RateLimitingBucketProvider {

    public static final int MAX_BANDWIDTH = 20;
    public static final int TOKEN_REFILL_COUNT_AT_ONCE = 20;
    public static final int TOKEN_REFILL_DURATION_MINUTES = 1;

    private final RateLimitingFactory rateLimitingFactory;

    /**
     * 고정된 대역폭 구성을 사용하여 토큰 버킷 생성
     *
     * @return 고정된 대역폭 구성을 사용하여 토큰 버킷
     */
    public Bucket generateBucket() {
        return rateLimitingFactory.generateBucket(
                MAX_BANDWIDTH, TOKEN_REFILL_COUNT_AT_ONCE, TOKEN_REFILL_DURATION_MINUTES);
    }

    /**
     * 대역폭 제한 목록을 사용하여 토큰 버킷 생성
     *
     * @param bandwidthList 대역폭 제한 구성 목록
     * @return 지정된 대역폭 제한으로 구성된 토큰 버킷
     */
    public Bucket generateComplexBucket(List<Bandwidth> bandwidthList) {
        return rateLimitingFactory.generateComplexBucket(bandwidthList);
    }
}

