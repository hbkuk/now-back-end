package com.now.service;

import com.now.domain.manager.Manager;
import com.now.dto.TokenClaims;
import com.now.exception.AuthenticationFailedException;
import com.now.repository.ManagerRepository;
import com.now.security.Authority;
import com.now.security.JwtTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 매니저 관련 비즈니스 로직을 처리하는 서비스
 */
@Service
@Slf4j
public class ManagerService {

    private ManagerRepository managerRepository;
    private PasswordEncoder passwordEncoder;
    private String appSalt;
    private JwtTokenService tokenProvider;
    private MessageSourceAccessor messageSource;

    @Autowired
    public ManagerService(ManagerRepository managerRepository, PasswordEncoder passwordEncoder,
                          @Value("${now.password.salt}") String appSalt, JwtTokenService tokenProvider,
                          MessageSourceAccessor messageSource) {
        this.managerRepository = managerRepository;
        this.passwordEncoder = passwordEncoder;
        this.appSalt = appSalt;
        this.tokenProvider = tokenProvider;
        this.messageSource = messageSource;
    }

    /**
     * 매니저의 인증을 처리하고 JWT 토큰을 생성하여 반환
     *
     * @param manager 로그인할 매니저 정보
     * @return 인증된 매니저에게 부여된 JWT(Json Web Token)
     * @throws AuthenticationFailedException 아이디 또는 비밀번호가 틀린 경우 발생하는 예외
     */
    public String generateAuthToken(Manager manager) {
        Manager savedManager = managerRepository.findById(manager.getId());

        if (savedManager == null) {
            throw new AuthenticationFailedException(messageSource.getMessage("error.authentication.failed"));
        }

        if (!passwordEncoder.matches(appendSalt(manager.getPassword()), savedManager.getPassword())) {
            throw new AuthenticationFailedException(messageSource.getMessage("error.authentication.failed"));
        }

        return tokenProvider.create(TokenClaims.create(Map.of(
                "id", manager.getId(), "role", Authority.MANAGER.getValue())));
    }

    /**
     * 매니저 아이디(authorId)에 해당하는 사용자를 조회 후 {@link Manager}를 반환
     *
     * @param managerId 조회할 매니저의 아이디
     * @return 매니저를 조회 후 {@link Manager}를 반환
     */
    public Manager findManagerById(String managerId) {
        Manager manager = managerRepository.findById(managerId);
        if (manager == null) {
            throw new AuthenticationFailedException(messageSource.getMessage("error.authentication.managerNotFound"));
        }
        return managerRepository.findById(managerId);
    }

    /**
     * 주어진 값에 암호화 솔트를 결합 후 문자열을 반환
     *
     * @param value 암호화 솔트를 추가할 값
     * @return 암호화 솔트가 추가된 값
     */
    private String appendSalt(String value) {
        return String.join("", appSalt, value);
    }
}
