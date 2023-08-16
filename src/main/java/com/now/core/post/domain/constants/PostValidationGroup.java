package com.now.core.post.domain.constants;

import org.springframework.validation.annotation.Validated;

/**
 * 유효성 검증 그룹을 위한 인터페이스를 정의한 클래스
 *
 * 객체의 유효성 검증 시, 특정 그룹을 지정하여 검증, {@link Validated} 어노테이션과 함께 사용
 */
public interface PostValidationGroup {

    interface getAllPosts {}

    interface saveNotice {}

    interface saveCommunity {}

    interface savePhoto {}

    interface saveInquiry {}

    interface saveAnswer {}

}

