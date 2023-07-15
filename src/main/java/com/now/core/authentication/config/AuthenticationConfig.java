package com.now.core.authentication.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * WebMvcConfigurer 인터페이스를 구현하여 인터셉터를 등록
 * JwtInterceptor를 사용하여 특정 URL 패턴에 대한 토큰 검증을 수행합니다.
 */
@Configuration
@RequiredArgsConstructor
public class AuthenticationConfig implements WebMvcConfigurer {

    private final JwtInterceptor jwtInterceptor;

    /**
     * 인터셉터를 등록하는 메서드
     *
     * @param registry 인터셉터 등록을 담당하는 InterceptorRegistry 객체
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/api/notice")
                .addPathPatterns("/api/community")
                .addPathPatterns("/api/photo")
                .addPathPatterns("/api/inquiry")
                .addPathPatterns("/api/answer/{inquiryIdx}");
    }
}


