package com.now.core.admin.authentication.domain;

import com.now.core.authentication.constants.Authority;
import lombok.*;

/**
 * 매니저의 정보를 담고있는 도메인 객체
 */
@Builder(toBuilder = true)
@ToString
@Getter
@EqualsAndHashCode
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class Manager {

    private final Authority authority = Authority.MANAGER;

    /**
     * 매니저의 고유 식별자
     */
    private final Integer managerIdx;

    /**
     * 매니저의 아이디
     */
    private final String id;

    /**
     * 매니저의 닉네임
     */
    private final String nickname;

    /**
     * 매니저의 이름
     */
    private final String name;

    /**
     * 매니저의 비밀번호
     */
    private String password;

    /**
     * 전달된 문자열이 현재 객체 필드의 id와 동일한다면 true 반환, 그렇지 않다면 false 반환
     *
     * @param managerId 매니저 아이디
     * @return 문자열이 현재 객체 필드의 id와 동일한다면 true 반환, 그렇지 않다면 false 반환
     */
    public boolean isSameManagerId(String managerId) {
        return this.id.equals(managerId);
    }
}
