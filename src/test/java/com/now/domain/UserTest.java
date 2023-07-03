package com.now.domain;

import com.now.domain.user.User;

public class UserTest {

    public static User newUser(String userId) {
        return User.builder()
                .id(userId)
                .password("testPassword")
                .name("testName")
                .nickname("testNickName")
                .build();
    }
}
