package com.now.core.member.presentation.dto;

import com.now.core.member.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 회원 정보를 응답으로 보내기 위한 클래스
 */
@Getter
@AllArgsConstructor
public class MemberProfile {

    private final String id;
    private final String nickname;
    private final String name;

    /**
     * Member 객체로부터 MemberProfile 객체를 생성하여 반환
     *
     * @param member Member 객체
     * @return 생성된 MemberProfile 객체
     */
    public static MemberProfile from(Member member) {
        return new MemberProfile(member.getId(), member.getNickname(), member.getName());
    }
}
