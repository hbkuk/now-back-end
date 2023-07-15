package com.now.core.member.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.now.core.authentication.constants.Authority;
import lombok.*;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * 회원의 정보를 담고있는 도메인 객체
 */
@Builder(toBuilder = true)
@ToString
@Getter
@EqualsAndHashCode
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class Member {

    private final Authority authority = Authority.MEMBER;

    /**
     * 비밀번호 정규식
     */
    private static final String PASSWORD_REGEX = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).*$";

    /**
     * 회원의 고유 식별자
     */
    private final Long memberIdx;

    /**
     * 회원의 아이디
     */
    @Size(groups = {MemberValidationGroup.signup.class}, max = 50, message = "ID는 최대 50자까지 입력 가능합니다.")
    private final String id;

    /**
     * 회원의 닉네임
     */
    @Size(groups = {MemberValidationGroup.signup.class}, max = 50, message = "닉네임은 최대 50자까지 입력 가능합니다.")
    private final String nickname;

    /**
     * 회원의 이름
     */
    @Size(groups = {MemberValidationGroup.signup.class}, max = 15, message = "이름은 최대 15자까지 입력 가능합니다.")
    private final String name;

    /**
     * 회원의 비밀번호
     */
    @Size(groups = {MemberValidationGroup.signup.class}, min = 4, max = 15, message = "패스워드는 4글자 이상, 15글자 이하여야 합니다")
    @Pattern(groups = {MemberValidationGroup.signup.class}, regexp = PASSWORD_REGEX, message = "패스워드는 영문, 숫자, 특수문자를 포함해야 합니다")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    /**
     * 전달된 비밀번호로 현재 객체를 수정 후 반환
     *
     * @param password 변경할 비밀번호
     * @return Member 회원 객체
     */
    public Member updatePassword(String password) {
        this.password = password;
        return this;
    }

    /**
     * 전달된 문자열이 현재 객체 필드의 id와 동일한다면 true 반환, 그렇지 않다면 false 반환
     *
     * @param memberId 사용자 아이디
     * @return 문자열이 현재 객체 필드의 id와 동일한다면 true 반환, 그렇지 않다면 false 반환
     */
    public boolean isSameMemberId(String memberId) {
        return this.id.equals(memberId);
    }
}
