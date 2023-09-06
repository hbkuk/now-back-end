package com.now.core.report.presentation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 컨트롤러 메서드의 파라미터에 사용되는 어노테이션
 *
 * 전송한 사용자의 정보를 주입받을 수 있도록 지원
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface SenderInfo {
}
