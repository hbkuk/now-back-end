package com.now.core.member.domain;

import org.springframework.validation.annotation.Validated;

/**
 * {@link Member} 유효성 검증 그룹을 위한 인터페이스를 정의한 클래스
 * {@link Member} 객체의 유효성 검증 시, 특정 그룹을 지정하여 검증
 * 이 인터페이스는 {@link Validated} 어노테이션과 함께 사용
 * 예를 들어, {@code @Validated(UserValidationGroup.signup.class)}와 같이 사용
 */
public interface MemberValidationGroup {
    /**
     * 회원 가입을 위한 유효성 검증 그룹
     */
    interface signup {}
}
