package com.now.common.logging;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * 로깅 관련 기능을 수행하는 Aspect
 *
 * 컨트롤러의 메서드 호출 및 응답 로그 기록
 */
@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Pointcut("@annotation(org.springframework.web.bind.annotation.PostMapping)")
    private void postMapping() {
    }

    @Pointcut("@annotation(org.springframework.web.bind.annotation.PutMapping)")
    private void putMapping() {
    }

    @Pointcut("execution(* com.now.core..presentation.*Controller.*(..))")
    private void controllerPointCut() {
    }

    @Pointcut("@annotation(org.springframework.web.bind.annotation.ExceptionHandler)")
    private void exceptionHandlerCut() {
    }

    /**
     * 컨트롤러의 POST 또는 PUT 메서드 호출 시 요청 로그를 기록
     *
     * @param joinPoint Aspect가 적용된 메서드의 조인 포인트
     */
    @Before("postMapping() || putMapping()")
    public void requestLog(final JoinPoint joinPoint) {
        Signature signature = joinPoint.getSignature();
        log.info("[ REQUEST ] Controller - {}, Method - {}, Arguments - {}",
                joinPoint.getTarget().getClass().getSimpleName(),
                signature.getName(),
                Arrays.toString(joinPoint.getArgs()));
    }

    /**
     * 컨트롤러의 메서드 호출 또는 예외 핸들러 메서드 실행 후 응답 로그 기록
     *
     * @param joinPoint Aspect가 적용된 메서드의 조인 포인트
     * @param response  응답 객체
     */
    @AfterReturning(value = "controllerPointCut() || exceptionHandlerCut()", returning = "response")
    public void responseLog(final JoinPoint joinPoint, final ResponseEntity<?> response) {
        Signature signature = joinPoint.getSignature();
        log.info("[ RESPONSE ] Controller - {}, Method - {}, returnBody - {}",
                joinPoint.getTarget().getClass().getSimpleName(),
                signature.getName(),
                response.getBody());
    }
}
