package com.now.repository;

import com.now.domain.user.User;
import com.now.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * 유저 정보를 관리하는 리포지토리
 *
 * @Repository
 * : 스프링 프레임워크에 해당 클래스가 데이터 액세스 계층의 리포지토리 역할을 수행하는 클래스임을 알림.
 */
@Repository
public class UserRepository {

    public UserMapper userMapper;

    @Autowired
    public UserRepository(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    /**
     * 사용자 정보를 삽입
     *
     * @param user 삽입할 사용자 정보
     */
    public void insert(User user) {
        userMapper.insert(user);
    }

    /**
     * 전달받은 아이디가 데이터베이스에 존재한다면 true 반환, 그렇지 않다면 false 반환
     *
     * @param id 확인할 아이디
     * @return 아이디의 존재 여부
     */
    public boolean existsById(String id) {
        return userMapper.existsById(id);
    }

    /**
     * 전달받은 닉네임이 데이터베이스에 존재한다면 true 반환, 그렇지 않다면 false 반환
     *
     * @param nickname 확인할 닉네임
     * @return 닉네임의 존재 여부
     */
    public boolean existsByNickname(String nickname) {
        return userMapper.existsByNickname(nickname);
    }
}
