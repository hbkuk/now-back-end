package com.now.repository;

import com.now.domain.manager.Manager;
import com.now.mapper.ManagerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * 매니저 정보를 관리하는 리포지토리
 *
 * @Repository
 * : 스프링 프레임워크에 해당 클래스가 데이터 액세스 계층의 리포지토리 역할을 수행하는 클래스임을 알림.
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
     * @param id 조회할 매니저의 아이디
     * @return 조회된 매니저 정보 (해당 아이디에 해당하는 매니저가 없으면 null)
     */
    public Manager findById(String id) {
        return managerMapper.findById(id);
    }
}
