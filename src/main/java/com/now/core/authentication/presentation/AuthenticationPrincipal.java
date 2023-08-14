package com.now.core.authentication.presentation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 컨트롤러 메서드의 파라미터에 사용되는 어노테이션
 *
 * 현재 사용자의 주체(principal) 정보를 주입받을 수 있도록 지원
 * 이 어노테이션을 사용한 파라미터에는 현재 회원의 주체 정보가 주입
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthenticationPrincipal {
}
