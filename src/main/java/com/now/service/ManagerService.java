package com.now.service;

import com.now.domain.manager.Manager;
import com.now.dto.TokenClaims;
import com.now.exception.AuthenticationFailedException;
import com.now.repository.ManagerRepository;
import com.now.security.Authority;
import com.now.security.JwtTokenService;
import com.now.security.PasswordSecurityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 매니저 관련 비즈니스 로직을 처리하는 서비스
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ManagerService {

    private final ManagerRepository managerRepository;
    private final PasswordSecurityManager passwordSecurityManager;
    private final JwtTokenService tokenProvider;
    private final MessageSourceAccessor messageSource;

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

        if (!passwordSecurityManager.matchesWithSalt(manager.getPassword(), savedManager.getPassword())) {
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
}
