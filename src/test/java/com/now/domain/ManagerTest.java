package com.now.domain;

import com.now.domain.manager.Manager;

public class ManagerTest {

    public static Manager createManagerByManagerId(String managerId) {
        return Manager.builder()
                .id(managerId)
                .password("managerPassword")
                .nickname("managerNickName")
                .build();
    }
}
