package com.now.domain.user;

import com.now.core.member.domain.Member;

public class MemberTest {

    public static Member createMember(String memberId) {
        return Member.builder()
                .id(memberId)
                .password("testPassword")
                .name("testName")
                .nickname("testNickName")
                .build();
    }
}
