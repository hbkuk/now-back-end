package com.now.service;

import com.now.domain.user.User;
import com.now.dto.UserDuplicateInfo;
import com.now.exception.DuplicateUserException;
import com.now.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 사용자 관련 비즈니스 로직을 처리하는 서비스
 */
@Service
public class UserService {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private String appSalt;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       @Value("${now.password.salt}") String appSalt) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.appSalt = appSalt;
    }

    /**
     * 사용자 정보를 등록하는 메서드
     *
     * @param user 등록할 사용자 정보
     * @throws DuplicateUserException 중복된 사용자 정보 예외
     */
    public void insert(User user) {
        UserDuplicateInfo duplicateUserInfo = duplicateUserCheck(user);

        if (duplicateUserInfo.existsAtLeastOneDuplicate()) {
            throw new DuplicateUserException(duplicateUserInfo.generateDuplicateFieldMessages());
        }

        userRepository.insert(user.updateByPassword(passwordEncoder.encode(user.getPassword() + appSalt)));
    }

    /**
     * 중복된 사용자 정보를 체크해서 {@link UserDuplicateInfo}를 반환
     *
     * @param user 체크할 사용자 정보
     * @return 중복된 사용자 정보 결과
     */
    private UserDuplicateInfo duplicateUserCheck(User user) {
        return UserDuplicateInfo.builder()
                .duplicateId(userRepository.existsById(user.getId()))
                .duplicateNickname(userRepository.existsByNickname(user.getNickname()))
                .build();
    }
}
