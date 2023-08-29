package com.now.common.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Arrays;
import java.util.Objects;

// Spring Cache 관련 Bean 설정
@Configuration
@EnableCaching  // Spring 캐싱 기능을 사용하기 위한 어노테이션
@EnableScheduling  // 스케줄링 기능을 사용하기 위한 어노테이션
@Slf4j  // 로깅을 위한 어노테이션
@Profile({"local", "dev", "prod"})
public class CachingConfig {

    public static final String POST_CACHE = "postCache";
    public static final String NOTICE_CACHE = "noticeCache";
    public static final String COMMUNITY_CACHE = "communityCache";
    public static final String PHOTO_CACHE = "photoCache";
    public static final String INQUIRY_CACHE = "inquiryCache";

    /**
     * Cache 관리자 빈을 생성하는 메소드
     *
     * @return CacheManager 인터페이스를 구현한 SimpleCacheManager 객체
     */
    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager simpleCacheManager = new SimpleCacheManager();
        simpleCacheManager.setCaches(Arrays.asList(
                new ConcurrentMapCache(POST_CACHE),
                new ConcurrentMapCache(NOTICE_CACHE),
                new ConcurrentMapCache(COMMUNITY_CACHE),
                new ConcurrentMapCache(PHOTO_CACHE),
                new ConcurrentMapCache(INQUIRY_CACHE)
        ));
        return simpleCacheManager;
    }

    /**
     * 모든 캐시 삭제
     */
    public void evictAllCaches() {
        // 모든 캐시를 순회하며 각 캐시를 비웁니다
        cacheManager().getCacheNames()
                .forEach(cacheName -> Objects.requireNonNull(cacheManager().getCache(cacheName)).clear());
    }

    /**
     * 주기적으로 모든 캐시를 삭제하는 스케줄링
     */
    @Scheduled(fixedRate = 300000)  // 300초마다 실행
    public void evictAllCachesAtIntervals() {
        evictAllCaches();
    }
}
