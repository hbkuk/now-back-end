package com.now.common.config.infrastructure;

import com.now.common.exception.TooManyRequestsException;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * Rate Limiting을 적용하는 인터셉터
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RateLimitingInterceptor implements HandlerInterceptor {

    private final RateLimitBucketMap rateLimitBucketMap;
    private final RateLimitingProvider rateLimitingProvider;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String ipAddress = request.getRemoteAddr();

        if (!rateLimitBucketMap.hasBucket(ipAddress)) { // IP 주소에 대한 버킷이 없을 경우, 새로운 버킷을 생성하고 저장
            Bucket newBucket = rateLimitingProvider.generateBucket();
            rateLimitBucketMap.addBucket(ipAddress, newBucket);
        }

        Bucket bucket = rateLimitBucketMap.getBucket(ipAddress);
        ConsumptionProbe probe = Objects.requireNonNull(bucket).tryConsumeAndReturnRemaining(1);

        if (probe.isConsumed()) {
            return true;
        }
        throw new TooManyRequestsException(probe.getNanosToWaitForRefill());
    }
}
