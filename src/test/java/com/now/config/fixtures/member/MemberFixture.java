package com.now.config.fixtures.member;

import com.now.core.member.domain.Member;
import com.now.core.member.presentation.dto.MemberProfile;

public class MemberFixture {

    public static String SAMPLE_MEMBER_ID_1 = "honi132";
    public static String SAMPLE_PASSWORD_1 = "testPassword1!";

    public static String TESTER1_ID = "TESTER_ID_1";
    public static String TESTER2_ID = "TESTER_ID_2";
    public static String TESTER3_ID = "TESTER_ID_3";
    public static String TESTER4_ID = "TESTER_ID_4";
    public static String TESTER5_ID = "TESTER_ID_5";

    public static String TESTER1_NAME = "TESTER_NAME_1";
    public static String TESTER2_NAME = "TESTER_NAME_2";
    public static String TESTER3_NAME = "TESTER_NAME_3";
    public static String TESTER4_NAME = "TESTER_NAME_4";
    public static String TESTER5_NAME = "TESTER_NAME_5";

    public static String TESTER1_NICKNAME = "TESTER_NIC_1";
    public static String TESTER2_NICKNAME = "TESTER_NIC_2";
    public static String TESTER3_NICKNAME = "TESTER_NIC_3";
    public static String TESTER4_NICKNAME = "TESTER_NIC_4";
    public static String TESTER5_NICKNAME = "TESTER_NIC_5";


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
