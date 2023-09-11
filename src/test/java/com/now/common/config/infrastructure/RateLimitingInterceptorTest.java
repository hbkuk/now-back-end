package com.now.common.config.infrastructure;

import com.now.config.document.utils.ControllerTest;
import com.now.core.post.common.presentation.dto.Condition;
import com.now.core.post.common.presentation.dto.constants.Sort;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.Duration;

import static com.now.common.config.infrastructure.RateLimitingBucketProvider.*;
import static com.now.config.fixtures.post.dto.ConditionFixture.createCondition;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ActiveProfiles("local")
@DisplayName("Rate Limiting Interceptor는")
class RateLimitingInterceptorTest extends ControllerTest {

    @Test
    @DisplayName("MAX_BANDWIDTH 보다 더 많은 요청을 할 경우 TooManyRequests 상태코드를 응답한다")
    void testRateLimitingInterceptor() throws Exception {
        // given
        String endpointPath = "/api/posts";
        Condition condition = createCondition(Sort.LATEST, 2);

        // 테스트 전에 RateLimitingProvider를 설정, Bucket 생성
        Bucket bucket = Bucket.builder()
                .addLimit(
                        Bandwidth.classic(MAX_BANDWIDTH,
                        Refill.intervally(TOKEN_REFILL_COUNT_AT_ONCE, Duration.ofMinutes(TOKEN_REFILL_DURATION_MINUTES))))
                .build();

        given(rateLimitBucketMap.hasBucket(any())).willReturn(false);
        given(rateLimitBucketMap.getBucket(any())).willReturn(bucket);

        // when
        for (int i = 0; i < MAX_BANDWIDTH; i++) {
            mockMvc.perform(MockMvcRequestBuilders.get(endpointPath)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("maxNumberOfPosts", String.valueOf(condition.getMaxNumberOfPosts())))
                    .andExpect(MockMvcResultMatchers.status().isOk());
        }

        // then
        mockMvc.perform(MockMvcRequestBuilders.get(endpointPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("maxNumberOfPosts", String.valueOf(condition.getMaxNumberOfPosts())))
                .andExpect(MockMvcResultMatchers.header().exists(RateLimitHeaders.RETRY_AFTER))
                .andExpect(MockMvcResultMatchers.status().isTooManyRequests());
    }

    @Test
    @DisplayName("지정된 토큰 리필 시간 이후에 요청을 다시 보내면 정상적으로 동작한다")
    void testRateLimitingAfterTokenRefill() throws Exception {
        // given
        String endpointPath = "/api/posts";
        Condition condition = createCondition(Sort.LATEST, 2);

        // 테스트 전에 RateLimitingProvider를 설정, Bucket 생성
        Bucket bucket = Bucket.builder()
                .addLimit(
                        Bandwidth.classic(MAX_BANDWIDTH,
                        Refill.intervally(TOKEN_REFILL_COUNT_AT_ONCE, Duration.ofMinutes(TOKEN_REFILL_DURATION_MINUTES))))
                .build();

        given(rateLimitBucketMap.hasBucket(any())).willReturn(false);
        given(rateLimitBucketMap.getBucket(any())).willReturn(bucket);

        // when
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < MAX_BANDWIDTH; i++) {
            mockMvc.perform(MockMvcRequestBuilders.get(endpointPath)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("maxNumberOfPosts", String.valueOf(condition.getMaxNumberOfPosts())))
                    .andExpect(MockMvcResultMatchers.status().isOk());
        }

        // 1분이 지나기 전에 한 번 더 요청을 보냄
        mockMvc.perform(MockMvcRequestBuilders.get(endpointPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("maxNumberOfPosts", String.valueOf(condition.getMaxNumberOfPosts())))
                .andExpect(MockMvcResultMatchers.status().isTooManyRequests());

        // 계산된 대기 시간 (1분) 이후에 다시 요청을 보냄
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - startTime;
        long waitTimeMillis = Duration.ofMinutes(TOKEN_REFILL_DURATION_MINUTES).toMillis() - elapsedTime;

        if (waitTimeMillis > 0) {
            Thread.sleep(waitTimeMillis);
        }

        // then
        for (int i = 0; i < TOKEN_REFILL_COUNT_AT_ONCE; i++) {
            mockMvc.perform(MockMvcRequestBuilders.get(endpointPath)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("maxNumberOfPosts", String.valueOf(condition.getMaxNumberOfPosts())))
                    .andExpect(MockMvcResultMatchers.status().isOk());
        }
    }

}
