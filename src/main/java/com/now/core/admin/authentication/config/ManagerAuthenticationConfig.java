package com.now.core.admin.authentication.config;

import com.now.core.admin.authentication.presentation.ManagerAuthenticationInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * WebMvcConfigurer 인터페이스를 구현하여 인터셉터를 등록
 */
@Configuration
@RequiredArgsConstructor
public class ManagerAuthenticationConfig implements WebMvcConfigurer {

    private final ManagerAuthenticationInterceptor managerAuthenticationInterceptor;

    /**
     * 인터셉터를 등록하는 메서드
     *
     * @param registry 인터셉터 등록을 담당하는 InterceptorRegistry 객체
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(managerAuthenticationInterceptor)
                .addPathPatterns("/api/manager/**")
                .excludePathPatterns("/api/manager/sign-in");
    }

}


