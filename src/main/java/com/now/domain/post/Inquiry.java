package com.now.domain.post;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.now.domain.permission.AccessPermission;
import com.now.exception.CannotViewInquiryException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * 문의 게시글을 나타내는 도메인 객체
 *
 * @SuperBuilder(toBuilder = true) : 상속 구조에서 부모 클래스의 빌더 패턴을 자동으로 생성해주는 기능.
 * toBuilder 옵션은 생성된 빌더 객체를 이용해 기존 객체를 복사하고 수정할 수 있도록 합니다.
 * @Getter : 필드들에 대한 Getter 메서드를 자동으로 생성합니다.
 * @ToString : 객체의 문자열 표현을 자동으로 생성합니다. 주요 필드들의 값을 포함한 문자열을 반환합니다.
 * @NoArgsConstructor(force = true)
 * : 매개변수가 없는 기본 생성자를 자동으로 생성합니다. MyBatis 또는 JPA 라이브러리에서는 기본 생성자를 필요로 합니다.
 * @AllArgsConstructor : 모든 필드를 매개변수로 받는 생성자를 자동으로 생성합니다.
 */
@SuperBuilder(toBuilder = true)
@Getter
@ToString
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class Inquiry extends Post {

    /**
     * 비밀글 설정 여부 (true: 비밀글)
     */
    private final boolean isSecret;

    /**
     * 답변 완료 여부 (true: 답변 완료)
     */
    private final boolean isAnswerCompleted;

    /**
     * 답변 관리자의 고유 식별자
     */
    private final String managerId;

    /**
     * 비밀글의 비밀번호
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private final String password;

    /**
     * 답변 내용
     */
    private final String answerContent;

    /**
     * 답변 일자
     */
    private final String answerRegDate;

    /**
     * 게시글을 조회할 수 있다면 true를 반환, 그렇지 않다면 예외를 던짐
     *
     * @param accessPermission 접근 권한을 확인하기 위한 AccessPermission 객체
     * @return 게시글을 조회할 수 있다면 true를 반환, 그렇지 않다면 예외를 던짐
     */
    public boolean canView(AccessPermission accessPermission) {
        if (isSecret) {
            return isAccessSecretBy(accessPermission);
        }
        return true;
    }

    /**
     * 비밀글에 접근할 수 있다면 true 반환, 그렇지 않다면 예외를 던짐
     *
     * @param accessPermission 접근 권한을 확인하기 위한 AccessPermission 객체
     * @return 비밀글에 접근할 수 있다면 true 반환, 그렇지 않다면 예외를 던짐
     */
    public boolean isAccessSecretBy(AccessPermission accessPermission) {
        if (!accessPermission.hasAccess(this.getAuthorId())) {
            throw new CannotViewInquiryException("다른 사용자가 작성한 문의글을 볼 수 없습니다.");
        }
        return true;
    }
}
