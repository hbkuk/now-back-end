package com.now.core.admin.authentication.presentation.dto;

import com.now.core.admin.authentication.domain.Manager;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 매니저 정보를 응답으로 보내기 위한 클래스
 */
@Getter
@AllArgsConstructor
public class ManagerProfile {

    private final String id;
    private final String nickname;

    /**
     * Manager 객체로부터 ManagerProfile 객체를 생성하여 반환
     *
     * @param manager Manager 객체
     * @return 생성된 ManagerProfile 객체
     */
    public static ManagerProfile from(Manager manager) {
        return new ManagerProfile(manager.getId(), manager.getNickname());
    }
}
