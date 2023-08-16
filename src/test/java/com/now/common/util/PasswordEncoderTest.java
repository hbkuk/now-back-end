package com.now.common.util;

import com.now.config.document.utils.BeanTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Password Encoder 객체")
public class PasswordEncoderTest extends BeanTest {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Nested
    @DisplayName("PasswordEncoder 객체의 matches 메서드는")
    class Matches_of {

        @Test
        @DisplayName("암호화 전 후의 raw 패스워드가 일치한다면 true를 반환한다.")
        void return_true_when_passwords_match() {
            // given
            String password = "password123";
            String encryptedPassword = passwordEncoder.encode(password);

            // when, then
            assertTrue(passwordEncoder.matches(password, encryptedPassword));
        }

        @Test
        @DisplayName("암호화 전 후의 raw 패스워드가 다르다면 false를 반환한다.")
        void return_false_when_passwords_do_not_match()  {
            // given
            String passwordA = "password123";
            String passwordB = "password123!";
            String encryptedPassword = passwordEncoder.encode(passwordB);

            // when, then
            assertFalse(passwordEncoder.matches(passwordA, encryptedPassword));
        }
    }

    @Test
    @DisplayName("다른 솔트 값을 사용한 암호화 결과는 다르다.")
    void different_salt_values_produce_different_encrypted_results(@Value("${now.password.salt}") String appSalt) {
        String password = "password123";
        String salt = "salt1";

        String encryptedPassword1 = passwordEncoder.encode(password + salt);
        String encryptedPassword2 = passwordEncoder.encode(password + appSalt);

        assertNotEquals(encryptedPassword1, encryptedPassword2);
    }
}
