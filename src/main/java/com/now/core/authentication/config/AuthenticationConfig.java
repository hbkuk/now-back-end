package com.now.core.authentication.config;

import com.now.core.authentication.presentation.AuthenticationArgumentResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * WebMvcConfigurer 인터페이스를 구현하여 인터셉터를 등록
 */
@Configuration
@RequiredArgsConstructor
public class AuthenticationConfig implements WebMvcConfigurer {

    private final AuthenticationInterceptor jwtInterceptor;
    private final ManagerInterceptor managerInterceptor;
    private final AuthenticationArgumentResolver authenticationArgumentResolver;

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

    /**
     * 컨트롤러 메서드의 Argument Resolver 등록
     *
     * @param resolvers 컨트롤러 메서드의 Argument Resolver 목록
     */
    @Override
    public void addArgumentResolvers(final List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(authenticationArgumentResolver);
    }

}


