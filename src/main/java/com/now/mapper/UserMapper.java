package com.now.mapper;

import com.now.domain.user.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 유저 정보에 접근하는 매퍼 인터페이스
 *
 * @Mapper
 * : MyBatis의 매퍼 인터페이스임을 나타냄.
 */
@Mapper
public interface UserMapper {

    /**
     * 사용자 정보를 삽입
     *
     * @param user 삽입할 사용자 정보
     */
    void insert(User user);

    /**
     * 전달받은 아이디가 데이터베이스에 존재한다면 true 반환, 그렇지 않다면 false 반환
     *
     * @param id 확인할 아이디
     * @return 아이디의 존재 여부
     */
    boolean existsById(String id);

    /**
     * 전달받은 닉네임이 데이터베이스에 존재한다면 true 반환, 그렇지 않다면 false 반환
     *
     * @param nickname 확인할 닉네임
     * @return 닉네임의 존재 여부
     */
    boolean existsByNickname(String nickname);

    /**
     * 전달받은 아이디에 해당하는 사용자 정보를 조회 후 반환
     *
     * @param id 조회할 사용자의 아이디
     * @return 조회된 사용자 정보 (해당 아이디에 해당하는 사용자가 없으면 null)
     */
    User findByUser(String id);
}
