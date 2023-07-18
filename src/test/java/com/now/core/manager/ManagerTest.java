package com.now.core.manager;

import com.now.core.admin.manager.domain.Manager;

public class ManagerTest {

    public static Manager createManager(String managerId) {
        return Manager.builder()
                .id(managerId)
                .password("managerPassword")
                .nickname("managerNickName")
                .build();
    }
}
