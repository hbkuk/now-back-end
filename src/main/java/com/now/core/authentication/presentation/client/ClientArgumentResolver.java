package com.now.core.authentication.presentation.client;

import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;

/**
 * 컨트롤러 메서드의 파라미터에 사용되는 어노테이션(@ClientPrincipal)과 연동하여,
 * 현재 클라이언트의 정보를 해당 파라미터에 주입하기 위한 핸들러 메서드 Argument Resolver
 */
@Component
@RequiredArgsConstructor
public class ClientArgumentResolver implements HandlerMethodArgumentResolver {

    private final ClientContext clientContext;

    /**
     * 주어진 메서드 파라미터가 @ClientPrincipal 어노테이션을 가지고 있다면 true, 그렇지 않다면 false 반환
     *
     * @param parameter 메서드 파라미터 정보
     * @return @ClientPrincipal 어노테이션을 가지고 있다면 true, 그렇지 않다면 false 반환
     */
    @Override
    public boolean supportsParameter(final MethodParameter parameter) {
        return parameter.hasParameterAnnotation(ClientPrincipal.class);
    }

    /**
     * supportsParameter 메서드에서 true를 반환한 경우,
     * 해당 파라미터에 현재 클라이언트의 주체 정보를 주입하여 반환
     *
     * @param parameter     메서드 파라미터 정보
     * @param mavContainer  ModelAndViewContainer 객체
     * @param webRequest    NativeWebRequest 객체
     * @param binderFactory WebDataBinderFactory 객체
     * @return 현재 클라이언트의 주체 정보
     */
    @Override
    public String resolveArgument(final MethodParameter parameter,
                                  final ModelAndViewContainer mavContainer,
                                  final NativeWebRequest webRequest,
                                  final WebDataBinderFactory binderFactory) {
        HttpServletRequest httpServletRequest = (HttpServletRequest) webRequest.getNativeRequest();
        clientContext.setPrincipal(httpServletRequest.getRemoteAddr());
        return clientContext.getPrincipal();
    }
}
