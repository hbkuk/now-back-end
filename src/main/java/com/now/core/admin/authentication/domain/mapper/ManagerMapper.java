package com.now.core.admin.authentication.domain.mapper;

import com.now.core.admin.authentication.domain.Manager;
import org.apache.ibatis.annotations.Mapper;

/**
 * 매니저 정보에 접근하는 매퍼 인터페이스
 */
@Mapper
public interface ManagerMapper {

    /**
     * 전달받은 아이디에 해당하는 매니저 정보를 조회 후 반환
     *
     * @param managerId 조회할 매니저의 아이디
     * @return 조회된 매니저 정보 (해당 아이디에 해당하는 매니저가 없으면 null)
     */
    Manager findById(String managerId);

    /**
     * 매니저 정보 저장
     *
     * @param manager 저장할 매니저 정보
     */
    void saveManager(Manager manager);
}
