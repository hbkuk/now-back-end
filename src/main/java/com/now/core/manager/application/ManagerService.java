package com.now.core.manager.application;

import com.now.common.exception.ErrorType;
import com.now.common.security.PasswordSecurityManager;
import com.now.core.authentication.application.JwtTokenService;
import com.now.core.authentication.application.dto.TokenClaims;
import com.now.core.authentication.constants.Authority;
import com.now.core.authentication.exception.InvalidAuthenticationException;
import com.now.core.manager.domain.Manager;
import com.now.core.manager.domain.ManagerRepository;
import com.now.core.manager.exception.InvalidManagerException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    /**
     * 매니저의 인증을 처리하고 JWT 토큰을 생성하여 반환
     *
     * @param manager 로그인할 매니저 정보
     * @return 인증된 매니저에게 부여된 JWT(Json Web Token)
     * @throws InvalidAuthenticationException 인증에 실패한 경우 발생하는 예외
     */
    public String generateAuthToken(Manager manager) {
        Manager savedManager = managerRepository.findById(manager.getId());

        if (savedManager == null) {
            throw new InvalidManagerException(ErrorType.NOT_FOUND_MANAGER);
        }

        if (!passwordSecurityManager.matchesWithSalt(manager.getPassword(), savedManager.getPassword())) {
            throw new InvalidManagerException(ErrorType.NOT_FOUND_MANAGER);
        }

        return tokenProvider.create(TokenClaims.create(Map.of(
                "id", manager.getId(), "role", Authority.MANAGER.getValue())));
    }

    /**
     * 전달받은 매니저 아이디(authorId)에 해당하는 매니저를 조회 후 {@link Manager}를 반환
     *
     * @param managerId 조회할 매니저의 아이디
     * @return 매니저를 조회 후 {@link Manager}를 반환
     */
    public Manager findManagerById(String managerId) {
        Manager manager = managerRepository.findById(managerId);
        if (manager == null) {
            throw new InvalidManagerException(ErrorType.NOT_FOUND_MANAGER);
        }
        return managerRepository.findById(managerId);
    }
}
