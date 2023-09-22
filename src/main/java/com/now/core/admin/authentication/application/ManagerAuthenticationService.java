package com.now.core.admin.authentication.application;

import com.now.common.exception.ErrorType;
import com.now.common.security.PasswordSecurityManager;
import com.now.core.admin.authentication.domain.Manager;
import com.now.core.admin.authentication.domain.ManagerRepository;
import com.now.core.admin.authentication.exception.InvalidManagerException;
import com.now.core.authentication.application.JwtTokenProvider;
import com.now.core.authentication.application.dto.TokenClaims;
import com.now.core.authentication.application.dto.jwtTokens;
import com.now.core.authentication.constants.Authority;
import com.now.core.member.exception.InvalidMemberException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class ManagerAuthenticationService {

    private final ManagerRepository managerRepository;
    private final PasswordSecurityManager passwordSecurityManager;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 매니저의 자격 증명을 확인하고, 유효한 매니저 정보를 반환
     *
     * @param manager 자격 증명을 확인할 매니저 객체
     * @return 유효한 매니저 정보를 담고 있는 객체
     */
    public Manager retrieveManager(Manager manager) {
        Manager savedManager = getManager(manager.getId());

        if (!passwordSecurityManager.matchesWithSalt(manager.getPassword(), savedManager.getPassword())) {
            throw new InvalidMemberException(ErrorType.NOT_FOUND_MEMBER);
        }
        return savedManager;
    }

    /**
     * 매니저의 정보를 기반으로 액세스 토큰과 리프레시 토큰 생성
     *
     * @param manager 토큰을 생성할 매니저 객체
     * @return 액세스 토큰과 리프레시 토큰을 담은 인증 토큰
     */
    public jwtTokens generateAuthToken(Manager manager) {
        return jwtTokens.builder()
                .accessToken(jwtTokenProvider.createAccessToken(generateTokenClaims(manager)))
                .refreshToken(jwtTokenProvider.createRefreshToken(generateTokenClaims(manager)))
                .build();
    }

    /**
     * 전달받은 매니저 아이디(authorId)에 해당하는 매니저를 조회 후 {@link Manager}를 반환
     *
     * @param managerId 조회할 매니저의 아이디
     * @return 매니저를 조회 후 {@link Manager}를 반환
     */
    public Manager getManager(String managerId) {
        Manager manager = managerRepository.findById(managerId);
        if (manager == null) {
            throw new InvalidManagerException(ErrorType.NOT_FOUND_MANAGER);
        }
        return managerRepository.findById(managerId);
    }

    /**
     * 전달받은 매니저 정보를 기반으로 클레임을 생성 후 반환
     *
     * @param manager 매니저 정보
     * @return 클레임을 생성 후 반환
     */
    private TokenClaims generateTokenClaims(Manager manager) { // TODO: Claims의 key값인 id, nickname, role에 대해서 강제할 방법
        return TokenClaims.create(
                Map.of("id", manager.getId(),
                        "nickname", manager.getNickname(),
                        "role", Authority.MANAGER.getValue()));
    }
}
