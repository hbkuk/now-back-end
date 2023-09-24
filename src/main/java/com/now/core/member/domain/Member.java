package com.now.core.member.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.now.core.authentication.constants.Authority;
import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
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

    /**
     * 비밀번호 정규식
     */
    private static final String ID_REGEX = "^[A-Za-z0-9]{1,50}$";
    private static final String NICKNAME_REGEX = "^[a-zA-Z가-힣]{1,50}$";
    private static final String NAME_REGEX = "^[a-zA-Z가-힣]{2,15}$";
    private static final String PASSWORD_REGEX = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{4,15}$";

    /**
     * 회원의 고유 식별자
     */
    @JsonIgnore
    private final Long memberIdx;

    /**
     * 회원의 아이디
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotNull(groups = {MemberValidationGroup.signup.class}, message = "{field.notnull}")
    @Pattern(groups = {MemberValidationGroup.signup.class}, regexp = ID_REGEX, message = "{member.id.pattern}")
    private final String id;

    /**
     * 회원의 닉네임
     */
    @NotNull(groups = {MemberValidationGroup.signup.class, MemberValidationGroup.update.class}, message = "{field.notnull}")
    @Pattern(groups = {MemberValidationGroup.signup.class}, regexp = NICKNAME_REGEX, message = "{member.nickname.pattern}")
    @Pattern(groups = {MemberValidationGroup.update.class}, regexp = NICKNAME_REGEX, message = "{member.nickname.pattern}")
    private final String nickname;

    /**
     * 회원의 이름
     */
    @NotNull(groups = {MemberValidationGroup.signup.class, MemberValidationGroup.update.class}, message = "{field.notnull}")
    @Pattern(groups = {MemberValidationGroup.signup.class}, regexp = NAME_REGEX, message = "{member.name.pattern}")
    @Pattern(groups = {MemberValidationGroup.update.class}, regexp = NAME_REGEX, message = "{member.name.pattern}")
    private final String name;

    /**
     * 회원의 비밀번호
     */
    @NotNull(groups = {MemberValidationGroup.signup.class}, message = "{field.notnull}")
    @Pattern(groups = {MemberValidationGroup.signup.class}, regexp = PASSWORD_REGEX, message = "{member.password.pattern}")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    /**
     * 불변객체 유지를 위한 회원 아이디를 업데이트 후 회원 객체 생성 후 반환
     * @param memberId 회원 아이디
     * @return 회원 객체
     */
    public Member updateMemberId(String memberId) {
        return Member.builder()
                .id(memberId)
                .nickname(this.nickname)
                .name(this.name)
                .password(this.password)
                .build();
    }

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
