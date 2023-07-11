package com.now.domain.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.now.domain.permission.AccessPermission;
import com.now.validation.UserValidationGroup;
import lombok.*;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * 유저 정보를 담고있는 도메인 객체
 *
 * @Builder(toBuilder = true)
 * : 빌더 패턴을 사용하여 객체를 생성합니다. toBuilder 옵션은 생성된 빌더 객체를 이용해 기존 객체를 복사하고 수정할 수 있도록 합니다.
 * @ToString : 객체의 문자열 표현을 자동으로 생성합니다. 주요 필드들의 값을 포함한 문자열을 반환합니다.
 * @Getter : 필드들에 대한 Getter 메서드를 자동으로 생성합니다.
 * @NoArgsConstructor(force = true)
 * : 매개변수가 없는 기본 생성자를 자동으로 생성합니다. MyBatis 또는 JPA 라이브러리에서는 기본 생성자를 필요로 합니다.
 * @AllArgsConstructor : 모든 필드를 매개변수로 받는 생성자를 자동으로 생성합니다.
 */
@Builder(toBuilder = true)
@ToString
@Getter
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class User implements AccessPermission {
    
    /**
     * 비밀번호 정규식
     */
    private static final String passwordRegex = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).*$";

    /**
     * 유저의 고유 식별자
     */
    private final Long userIdx;

    /**
     * 유저의 아이디
     */
    @Size(groups = {UserValidationGroup.signup.class}, max = 50, message = "ID는 최대 50자까지 입력 가능합니다.")
    private final String id;

    /**
     * 유저의 비밀번호
     */
    @Size(groups = {UserValidationGroup.signup.class}, min = 4, max = 15, message = "패스워드는 4글자 이상, 15글자 이하여야 합니다")
    @Pattern(groups = {UserValidationGroup.signup.class}, regexp = passwordRegex, message = "패스워드는 영문, 숫자, 특수문자를 포함해야 합니다")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    /**
     * 유저의 이름
     */
    @Size(groups = {UserValidationGroup.signup.class}, max = 15, message = "이름은 최대 15자까지 입력 가능합니다.")
    private final String name;

    /**
     * 유저의 닉네임
     */
    @Size(groups = {UserValidationGroup.signup.class}, max = 50, message = "닉네임은 최대 50자까지 입력 가능합니다.")
    private final String nickname;

    /**
     * 전달된 비밀번호로 현재 객체를 수정 후 반환
     *
     * @param password 변경할 비밀번호
     * @return User 도메인 객체
     */
    public User updateByPassword(String password) {
        this.password = password;
        return this;
    }

    /**
     * 전달된 문자열이 현재 객체 필드의 id와 동일한다면 true 반환, 그렇지 않다면 false 반환
     *
     * @param userId 사용자 식별자
     * @return 문자열이 현재 객체 필드의 id와 동일한다면 true 반환, 그렇지 않다면 false 반환
     */
    public boolean isSameUserId(String userId) {
        return this.id.equals(userId);
    }

    /**
     * 전달된 사용자 아이디를 확인해서 동일하다면 true 반환, 그렇지 않다면 false 반환
     *
     * @param userId 사용자 아이디
     * @return 사용자 아이디를 확인해서 동일하다면 true 반환, 그렇지 않다면 false 반환
     */
    @Override
    public boolean hasAccess(String userId) {
        return this.isSameUserId(userId);
    }
}
