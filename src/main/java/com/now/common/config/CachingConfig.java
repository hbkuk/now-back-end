package com.now.common.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
@EnableCaching
public class CachingConfig {

    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager simpleCacheManager = new SimpleCacheManager();
        simpleCacheManager.setCaches( Arrays.asList(new ConcurrentMapCache("postCache"),
                new ConcurrentMapCache("noticeCache"), new ConcurrentMapCache("communityCache"),
                new ConcurrentMapCache("photoCache"), new ConcurrentMapCache("inquiryCache")));
        return simpleCacheManager;
    }
}