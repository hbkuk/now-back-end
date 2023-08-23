package com.now.config.fixtures.member;

import com.now.core.member.domain.Member;

public class MemberFixture {

    public static String SAMPLE_MEMBER_ID_1 = "honi132";
    public static String SAMPLE_PASSWORD_1 = "testPassword1!";

    public static String MEMBER1_ID = "MEMBER_ID_1";
    public static String MEMBER2_ID = "MEMBER_ID_2";
    public static String MEMBER3_ID = "MEMBER_ID_3";
    public static String MEMBER4_ID = "MEMBER_ID_4";
    public static String MEMBER5_ID = "MEMBER_ID_5";

    public static String MEMBER1_NAME = "MEMBER_NAME_1";
    public static String MEMBER2_NAME = "MEMBER_NAME_2";
    public static String MEMBER3_NAME = "MEMBER_NAME_3";
    public static String MEMBER4_NAME = "MEMBER_NAME_4";
    public static String MEMBER5_NAME = "MEMBER_NAME_5";

    public static String MEMBER1_NICKNAME = "MEMBER_NIC_1";
    public static String MEMBER2_NICKNAME = "MEMBER_NIC_2";
    public static String MEMBER3_NICKNAME = "MEMBER_NIC_3";
    public static String MEMBER4_NICKNAME = "MEMBER_NIC_4";
    public static String MEMBER5_NICKNAME = "MEMBER_NIC_5";

    public static Member createMember(Long memberIdx, String memberId) {
        return Member.builder()
                .memberIdx(memberIdx)
                .id(memberId)
                .password("testPassword")
                .name("testName")
                .nickname("testNickName")
                .build();
    }

    public static Member createMember(String memberId) {
        return Member.builder()
                .id(memberId)
                .password("testPassword")
                .name("testName")
                .nickname("testNickName")
                .build();
    }

    public static Member createMember(String memberId, String name, String nickname) {
        return Member.builder()
                .id(memberId)
                .name(name)
                .nickname(nickname)
                .password(SAMPLE_PASSWORD_1)
                .build();
    }

    public static Member createMember(String id, String password) {
        return Member.builder()
                .id(id)
                .password(password)
                .build();
    }

    public static Member createMemberProfile(String id, String nickname, String name) {
        return Member.builder()
                .id(id)
                .nickname(nickname)
                .name(name)
                .build();
    }
}
