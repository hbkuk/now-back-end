package com.now.common.config.infrastructure;

import io.github.bucket4j.Bucket;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * IP 주소와 Rate Limit 버킷을 관리하는 클래스
 */
@Component
@RequiredArgsConstructor
public class RateLimitBucketMap {
    private final Map<String, Bucket> bucketConcurrentHashMap = new ConcurrentHashMap<>();

    /**
     * 주어진 IP 주소에 대한 버킷이 존재하면 true 반환, 그렇지 않다면 false 반환
     *
     * @param ipAddress IP 주소
     * @return 버킷이 존재하면 true, 그렇지 않으면 false 반환
     */
    public boolean hasBucket(String ipAddress) {
        return bucketConcurrentHashMap.containsKey(ipAddress);
    }

    /**
     * 주어진 IP 주소에 대한 버킷 반환
     *
     * @param ipAddress IP 주소
     * @return IP 주소에 대한 버킷 객체
     */
    public Bucket getBucket(String ipAddress) {
        return bucketConcurrentHashMap.get(ipAddress);
    }

    /**
     * 주어진 IP 주소에 새로운 버킷 추가
     *
     * @param ipAddress IP 주소
     * @param bucket    추가할 버킷 객체
     */
    public void addBucket(String ipAddress, Bucket bucket) {
        bucketConcurrentHashMap.put(ipAddress, bucket);
    }

    /**
     * 주어진 IP 주소에 대한 버킷 제거
     *
     * @param ipAddress IP 주소
     */
    public void removeBucket(String ipAddress) {
        bucketConcurrentHashMap.remove(ipAddress);
    }
}
