package com.now.config.fixtures.manager;

import com.now.core.admin.authentication.domain.Manager;

public class ManagerFixture {

    public static String SAMPLE_MEMBER_ID_1 = "honi132";
    public static String SAMPLE_PASSWORD_1 = "testPassword1!";

    public static String MANAGER1_ID = "MANAGER_ID_1";
    public static String MANAGER2_ID = "MANAGER_ID_2";
    public static String MANAGER3_ID = "MANAGER_ID_3";
    public static String MANAGER4_ID = "MANAGER_ID_4";
    public static String MANAGER5_ID = "MANAGER_ID_5";
    public static String MANAGER6_ID = "MANAGER_ID_6";

    public static String MANAGER1_NAME = "MANAGER_NAME_1";
    public static String MANAGER2_NAME = "MANAGER_NAME_2";
    public static String MANAGER3_NAME = "MANAGER_NAME_3";
    public static String MANAGER4_NAME = "MANAGER_NAME_4";
    public static String MANAGER5_NAME = "MANAGER_NAME_5";
    public static String MANAGER6_NAME = "MANAGER_NAME_6";

    public static String MANAGER1_NICKNAME = "MANAGER_NIC_1";
    public static String MANAGER2_NICKNAME = "MANAGER_NIC_2";
    public static String MANAGER3_NICKNAME = "MANAGER_NIC_3";
    public static String MANAGER4_NICKNAME = "MANAGER_NIC_4";
    public static String MANAGER5_NICKNAME = "MANAGER_NIC_5";
    public static String MANAGER6_NICKNAME = "MANAGER_NIC_6";


    public static Manager createManager(String memberId) {
        return Manager.builder()
                .id(memberId)
                .password("testPassword")
                .name("testName")
                .nickname("testNickName")
                .build();
    }

    public static Manager createManager(String memberId, String name, String nickname) {
        return Manager.builder()
                .id(memberId)
                .name(name)
                .nickname(nickname)
                .password(SAMPLE_PASSWORD_1)
                .build();
    }

    public static Manager createManager(String id, String password) {
        return Manager.builder()
                .id(id)
                .password(password)
                .build();
    }

    public static Manager createManagerProfile(String id, String nickname, String name) {
        return Manager.builder()
                .id(id)
                .nickname(nickname)
                .name(name)
                .build();
    }
}
