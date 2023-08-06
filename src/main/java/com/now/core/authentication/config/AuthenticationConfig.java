package com.now.core.authentication.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * WebMvcConfigurer 인터페이스를 구현하여 인터셉터를 등록
 */
@Configuration
@RequiredArgsConstructor
public class AuthenticationConfig implements WebMvcConfigurer {

    private final AuthenticationInterceptor jwtInterceptor;
    private final ManagerInterceptor managerInterceptor;

    /**
     * 인터셉터를 등록하는 메서드
     *
     * @param registry 인터셉터 등록을 담당하는 InterceptorRegistry 객체
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/manager/**")
                .excludePathPatterns("/api/sign-up")
                .excludePathPatterns("/api/sign-in")
                .excludePathPatterns("/api/log-out")
                .excludePathPatterns("/api/refresh")
                .excludePathPatterns("/api/inquiries/secret/**")
                .excludePathPatterns("/api/member/me");


        registry.addInterceptor(managerInterceptor)
                .addPathPatterns("/api/manager/**")
                .excludePathPatterns("/api/manager/login");
    }
}


