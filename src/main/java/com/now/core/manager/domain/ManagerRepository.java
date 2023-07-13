package com.now.core.manager.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * 매니저 정보를 관리하는 레포지토리
 */
@Repository
public class ManagerRepository {

    public ManagerMapper managerMapper;

    @Autowired
    public ManagerRepository(ManagerMapper managerMapper) {
        this.managerMapper = managerMapper;
    }

    /**
     * 전달받은 아이디에 해당하는 매니저 정보를 조회 후 반환
     *
     * @param managerId 조회할 매니저의 아이디
     * @return 조회된 매니저 정보 (해당 아이디에 해당하는 매니저가 없으면 null)
     */
    public Manager findById(String managerId) {
        return managerMapper.findById(managerId);
    }
}
