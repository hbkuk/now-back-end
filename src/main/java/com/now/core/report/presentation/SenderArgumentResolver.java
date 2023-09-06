package com.now.core.report.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;

/**
 * 컨트롤러 메서드의 파라미터에 사용되는 어노테이션(@SenderInfo)과 연동하여,
 * 현재 사용자의 ip 정보를 해당 파라미터에 주입하기 위한 핸들러 메서드 Argument Resolver
 */
@Component
@RequiredArgsConstructor
public class SenderArgumentResolver implements HandlerMethodArgumentResolver {

    private final SenderContext senderContext;

    /**
     * 주어진 메서드 파라미터가 @SenderInfo 어노테이션을 가지고 있다면 true, 그렇지 않다면 false 반환
     *
     * @param parameter 메서드 파라미터 정보
     * @return @SenderInfo 어노테이션을 가지고 있다면 true, 그렇지 않다면 false 반환
     */
    @Override
    public boolean supportsParameter(final MethodParameter parameter) {
        return parameter.hasParameterAnnotation(SenderInfo.class);
    }

    /**
     * supportsParameter 메서드에서 true를 반환한 경우,
     * 해당 파라미터에 현재 사용자의 ip 정보를 주입하여 반환
     *
     * @param parameter       메서드 파라미터 정보
     * @param mavContainer    ModelAndViewContainer 객체
     * @param webRequest      NativeWebRequest 객체
     * @param binderFactory  WebDataBinderFactory 객체
     * @return 현재 사용자의 주체 정보
     */
    @Override
    public String resolveArgument(final MethodParameter parameter,
                                  final ModelAndViewContainer mavContainer,
                                  final NativeWebRequest webRequest,
                                  final WebDataBinderFactory binderFactory) {
        HttpServletRequest httpServletRequest = (HttpServletRequest) webRequest.getNativeRequest();
        senderContext.setIpAddress(httpServletRequest.getRemoteAddr());
        return senderContext.getIpAddress();
    }
}
