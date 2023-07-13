package com.now.common.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 패스워드의 암호화, 암호화된 패스워드의 검증 등을 처리하는 컴포넌트
 */
@Slf4j
@Component
public class PasswordSecurityManager {

    private final PasswordEncoder passwordEncoder;
    private final String salt;

    @Autowired
    public PasswordSecurityManager(PasswordEncoder passwordEncoder, @Value("${now.password.salt}") String salt) {
        this.passwordEncoder = passwordEncoder;
        this.salt = salt;
    }

    /**
     * 전달받은 값에 암호화 솔트를 추가하여 암호화된 값 반환
     *
     * @param value 암호화할 값
     * @return 암호화된 값
     */
    public String encodeWithSalt(String value) {
        return passwordEncoder.encode(appendSalt(value));
    }

    /**
     * 주어진 값과 암호화 솔트를 추가하여 저장된 암호화된 값과 일치한다면 true 반환, 그렇지 않다면 false 반환
     *
     * @param rawValue        비교할 원본 값
     * @param encodedPassword 저장된 암호화된 값
     * @return 값이 일치하는 경우 true, 그렇지 않은 경우 false
     */
    public boolean matchesWithSalt(String rawValue, String encodedPassword) {
        String valueWithSalt = appendSalt(rawValue);
        return passwordEncoder.matches(valueWithSalt, encodedPassword);
    }

    /**
     * 주어진 값에 암호화 솔트를 결합 후 문자열을 반환
     *
     * @param value 암호화 솔트를 추가할 값
     * @return 암호화 솔트가 추가된 값
     */
    private String appendSalt(String value) {
        return String.join("", salt, value);
    }
}
