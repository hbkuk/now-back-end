package com.now.common.config.infrastructure;

import com.now.common.exception.TooManyRequestsException;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
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
        if (shouldSkipRequest(request.getMethod())) {
            return true;
        }

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

    /**
     * Skip 가능하다면 true 반환, 그렇지 않다면 false 반환(GET 및 OPTIONS 요청인 경우 true)
     *
     * @param httpMethod HTTP 요청 메서드
     * @return Skip 가능하다면 true 반환, 그렇지 않다면 false 반환(GET 및 OPTIONS 요청인 경우 true)
     */
    private boolean shouldSkipRequest(String httpMethod) {
        return isOptionMethod(httpMethod);
    }

    /**
     * HTTP 요청 메서드가 OPTIONS인지 확인
     *
     * @param httpMethod 확인할 HTTP 요청 메서드
     * @return HTTP 요청 메서드가 OPTIONS인 경우 true, 그렇지 않은 경우 false
     */
    private boolean isOptionMethod(String httpMethod) {
        return HttpMethod.valueOf(httpMethod) == HttpMethod.OPTIONS;
    }
}
