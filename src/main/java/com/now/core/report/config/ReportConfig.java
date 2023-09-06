package com.now.core.report.config;

import com.now.core.report.presentation.SenderArgumentResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * WebMvcConfigurer 인터페이스를 구현하여 인터셉터를 등록
 */
@Configuration
@RequiredArgsConstructor
public class ReportConfig implements WebMvcConfigurer {

    private final SenderArgumentResolver senderArgumentResolver;

    /**
     * 컨트롤러 메서드의 Argument Resolver 등록
     *
     * @param resolvers 컨트롤러 메서드의 Argument Resolver 목록
     */
    @Override
    public void addArgumentResolvers(final List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(senderArgumentResolver);
    }

}


